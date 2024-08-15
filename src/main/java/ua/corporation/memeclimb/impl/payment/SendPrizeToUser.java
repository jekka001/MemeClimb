package ua.corporation.memeclimb.impl.payment;

import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.types.config.Commitment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sol4k.Connection;
import org.sol4k.exception.RpcException;
import org.sol4k.instruction.Instruction;
import ua.corporation.memeclimb.entity.main.PaymentInformation;
import ua.corporation.memeclimb.entity.main.response.helpEntity.SwapResponse;
import ua.corporation.memeclimb.exception.PayPrizeException;
import ua.corporation.memeclimb.exception.RecallException;
import ua.corporation.memeclimb.exception.ServerHasNotMoneyException;
import ua.corporation.memeclimb.impl.BasePayment;
import ua.corporation.memeclimb.impl.CheckOperation;
import ua.corporation.memeclimb.impl.PaymentOperation;
import ua.corporation.memeclimb.mapper.TransactionMapper;
import ua.corporation.memeclimb.service.JupiterService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class SendPrizeToUser extends BasePayment implements PaymentOperation {
    private static final Logger logger = LoggerFactory.getLogger(SendPrizeToUser.class.getName());
    private final JupiterService jupiterService;
    private final PaymentInformation paymentInformation;
    private final ReentrantLock lock;
    private final TransactionMapper transactionMapper;
    private final String userSplTokenAddress;
    private final CheckOperation hashChecker;


    public SendPrizeToUser(Connection sol4kConnection, RpcClient solanajClient, JupiterService jupiterService,
                           TransactionMapper transactionMapper, PaymentInformation paymentInformation,
                           ReentrantLock lock, String userSplTokenAddress) {
        super(sol4kConnection, transactionMapper);
        this.jupiterService = jupiterService;
        this.paymentInformation = paymentInformation;
        this.lock = lock;
        this.transactionMapper = transactionMapper;
        this.userSplTokenAddress = userSplTokenAddress;
        this.hashChecker = new HashChecker(solanajClient, paymentInformation.getPayerPublicKey(), paymentInformation.getUserId(),
                10, Commitment.CONFIRMED);

    }

    public void run() {
        try {
            logger.info("Start pay to user " + paymentInformation.getUserId() + " coin - " + paymentInformation.getSymbol() + " amount = " + paymentInformation.getAmount() + " as prize");
            lock.lock();
            sendPrize();
            lock.unlock();
            logger.info("Finished pay to user " + paymentInformation.getUserId() + " coin - " + paymentInformation.getSymbol() + " amount = " + paymentInformation.getAmount() + " as prize");
        } catch (Throwable e) {
            lock.unlock();
            logger.error("Server can't pay to user " + paymentInformation.getUserId() + " coin - " + paymentInformation.getSymbol() + " amount = " + paymentInformation.getAmount() + " as prize");
            throw new PayPrizeException(e);
        }
    }

    private void sendPrize() {
        int counter = 0;
        sendSwapTransaction(counter, 3);
    }

    private void sendSwapTransaction(int counter, int reCallCounter) {
        String catchHash = null;
        try {
            String hash = swapTransaction();
            catchHash = hash;
            Thread.sleep(10000);
            logger.info("Try check Swap transaction for user - " + paymentInformation.getUserId() + " and for coin - " + paymentInformation.getSymbol());

            hashChecker.check(hash);
        } catch (Exception e) {
            if (e instanceof RpcException && ((RpcException) e).getRawResponse().contains("Error: insufficient funds")) {
                logger.error("Server didn't have enough money for pay user - " + paymentInformation.getUserId() + " coin - " + paymentInformation.getSymbol() + " amount - " + paymentInformation.getAmount());

                throw new ServerHasNotMoneyException("In the server account not enough usdt for pay user - " + paymentInformation.getUserId() + "token - " + paymentInformation.getSymbol() + "in usdPrize - " + paymentInformation.getUsdPrize());
            } else if (isProblemWithBlockchainSwap(e)) {
                retryCall(counter, reCallCounter, e);
            } else if (e.getMessage().contains("We don't find this hash - ") ||
                    (e instanceof RpcException && ((RpcException) e).getRawResponse().contains("Timestamp should be greater than the last updated timestamp."))) {
                try {
                    hashChecker.check(catchHash);
                    retryCall(counter, reCallCounter, e);
                } catch (org.p2p.solanaj.rpc.RpcException ex) {
                    throw new RuntimeException(ex);
                }
            }

            logger.error("Server problem with pay user - " + paymentInformation.getUserId() + " coin - " + paymentInformation.getSymbol() + " amount - " + paymentInformation.getAmount() + " error -" + e);
            throw new PayPrizeException(e);
        }
    }

    private boolean isProblemWithBlockchainSwap(Exception e) {
        return e.getMessage() != null && (
                e.getMessage().contains("Error processing Instruction ") ||
                        e.getMessage().contains("Blockhash not found") ||
                        e.getMessage().contains("Node is behind by") ||
                        e.getMessage().contains("Server returned HTTP response code: 429") ||
                        e.getMessage().contains("Attempt to debit an account but found no record of a prior credit")
        );
    }

    private void retryCall(int counter, int reCallCounter, Exception e) {
        if (reCallCounter != counter) {
            try {
                Thread.sleep(5000);
                counter++;
                sendSwapTransaction(counter, reCallCounter);
            } catch (InterruptedException ex) {
                logger.error("Server problem with pay user - " + paymentInformation.getUserId() + " coin - " + paymentInformation.getSymbol() + " amount - " + paymentInformation.getAmount());

                throw new RecallException("Some problem with Thread - " + ex);
            }
        } else {
            logger.error("Blockchain problem with pay user - " + paymentInformation.getUserId() + " coin - " + paymentInformation.getSymbol() + " amount - " + paymentInformation.getAmount() + e);
            throw new RecallException("We retry many time but have some problem - " + e.getMessage());
        }
    }

    private String swapTransaction() {
        logger.info("Send swap transaction for user - " + paymentInformation.getUserId() + " and for coin - " + paymentInformation.getSymbol());

        return sendPaymentTransaction(paymentInformation);
    }

    @Override
    protected List<Instruction> getPaymentInstructions(PaymentInformation paymentInformation) {
        SwapResponse swapResponse =
                jupiterService.getSwapTransaction(
                        paymentInformation.getCoin(),
                        paymentInformation.getPayerPublicKey(),
                        paymentInformation.getUsdPrize(),
                        userSplTokenAddress);

        List<Instruction> computeBudgetInstructions = transactionMapper.fromListCustom(swapResponse.getComputeBudgetInstructions());
        Instruction swapInstruction = transactionMapper.fromCustom(swapResponse.getSwapInstruction());

        List<Instruction> instructions = new ArrayList<>(computeBudgetInstructions);
        instructions.add(swapInstruction);

        return instructions;
    }
}
