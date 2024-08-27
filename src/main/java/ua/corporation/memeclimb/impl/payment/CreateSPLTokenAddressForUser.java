package ua.corporation.memeclimb.impl.payment;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.types.config.Commitment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sol4k.Connection;
import org.sol4k.exception.RpcException;
import org.sol4k.instruction.Instruction;
import ua.corporation.memeclimb.entity.main.PaymentInformation;
import ua.corporation.memeclimb.exception.*;
import ua.corporation.memeclimb.impl.BasePayment;
import ua.corporation.memeclimb.impl.CheckOperation;
import ua.corporation.memeclimb.impl.PaymentOperation;
import ua.corporation.memeclimb.mapper.TransactionMapper;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class CreateSPLTokenAddressForUser extends BasePayment implements PaymentOperation {
    private static final Logger logger = LoggerFactory.getLogger(CreateSPLTokenAddressForUser.class.getName());
    private final RpcClient solanajClient;
    private final PaymentInformation paymentInformation;
    private final ReentrantLock lock;
    private PublicKey userSplTokenAddress;
    private final CheckOperation hashChecker;


    public CreateSPLTokenAddressForUser(Connection sol4kConnection, RpcClient solanajClient, TransactionMapper transactionMapper,
                                        PaymentInformation paymentInformation, ReentrantLock lock) {
        super(sol4kConnection, transactionMapper);
        this.paymentInformation = paymentInformation;
        this.lock = lock;
        this.solanajClient = solanajClient;
        this.hashChecker = new HashChecker(solanajClient, paymentInformation.getPayerPublicKey(), paymentInformation.getUserId(),
                10, Commitment.FINALIZED);
    }

    public String run() {
        try {
            logger.info("Start pay to user " + paymentInformation.getUserId() + " coin - " + paymentInformation.getSymbol() + " amount = " + paymentInformation.getAmount() + " as prize");
            lock.lock();
            createSPLTokenAddress();
            lock.unlock();
            logger.info("Finished pay to user " + paymentInformation.getUserId() + " coin - " + paymentInformation.getSymbol() + " amount = " + paymentInformation.getAmount() + " as prize");
            return userSplTokenAddress.toString();
        } catch (Throwable e) {
            lock.unlock();
            logger.error("Server can't pay to user " + paymentInformation.getUserId() + " coin - " + paymentInformation.getSymbol() + " amount = " + paymentInformation.getAmount() + " as prize " + e.getMessage());
            throw new PayPrizeException(e);
        }
    }

    private void createSPLTokenAddress() {
        int counter = 0;
        userSplTokenAddress = createSplToken(counter, 3);
    }

    private PublicKey createSplToken(int counter, int reCallCounter) {
        try {
            String hash = sendPaymentTransaction(paymentInformation);
            Thread.sleep(5000);
            hashChecker.check(hash);
            logger.info("Token address for coin - " + paymentInformation.getSymbol() + " and for user - " + paymentInformation.getUserId() + " was create");
            return getSplTokenAccount();
        } catch (Exception exe) {
            return tryToFixProblem(counter, reCallCounter, exe);
        }
    }

    private PublicKey tryToFixProblem(int counter, int reCallCounter, Exception exe) {
        if (exe instanceof RpcException && ((RpcException) exe).getRawResponse().contains("with insufficient funds for rent")) {
            logger.error("Server didn't have enough money for pay user - " + paymentInformation.getUserId() + " coin - " + paymentInformation.getSymbol() + " amount - " + paymentInformation.getAmount());

            throw new ServerHasNotMoneyException("In the server account not enough usdt for pay user - " + paymentInformation.getUserId() + " token - " + paymentInformation.getSymbol() + "in usdPrize - " + paymentInformation.getUsdPrize());
        } else if (exe instanceof RpcException && ((RpcException) exe).getRawResponse().contains("Provided owner is not allowed")) {
            return skipCreationAndTryGet();
        } else if (isProblemWithBlockchain(exe)) {
            return retryCall(counter, reCallCounter, exe);
        } else {
            logger.error("Server problem with creation Spl Token");
            throw new CreateSPLTokenException("Some problem with server - " + exe.getMessage());
        }
    }

    private PublicKey skipCreationAndTryGet() {
        logger.error("Token address for coin - " + paymentInformation.getSymbol() + " and for user - " + paymentInformation.getUserId() + " was create and we can't create again.");
        try {
            return getSplTokenAccount();
        } catch (org.p2p.solanaj.rpc.RpcException e) {
            logger.error("Token address for coin - " + paymentInformation.getSymbol() + " and for user - " + paymentInformation.getUserId() + " was create and we can't create again.");
            throw new SPLTokenExistException("Token account is exist");
        }
    }

    @Override
    protected List<Instruction> getPaymentInstructions(PaymentInformation paymentInformation) {
        List<Instruction> instructions =
                getComputerBudgetInstructions(paymentInformation.getUnitLimit(), paymentInformation.getUnitPrice());
        Instruction accountInstruction = createAssociatedInstruction(paymentInformation);

        instructions.add(accountInstruction);

        return instructions;
    }

    private PublicKey getSplTokenAccount() throws org.p2p.solanaj.rpc.RpcException {
        return solanajClient.getApi().getTokenAccountsByOwner(
                new PublicKey(paymentInformation.getReceiverPublicKey()),
                new PublicKey(paymentInformation.getMint())
        );
    }

    private boolean isProblemWithBlockchain(Exception exe) {
        return exe.getMessage() != null && (
                exe.getMessage().contains("Blockhash not found") ||
                        exe.getMessage().contains("Node is behind by") ||
                        exe.getMessage().contains("Server returned HTTP response code: 429") ||
                        exe.getMessage().contains("We don't find this hash")
        );
    }

    private PublicKey retryCall(int counter, int reCallCounter, Exception exe) {
        if (reCallCounter != counter) {
            counter++;
            logger.info("Try create Spl Token Address for user - " + paymentInformation.getUserId() + " and for coin - " + paymentInformation.getSymbol());
            return createSplToken(counter, reCallCounter);
        } else {
            logger.error("Blockchain problem create Spl Token Address for user - " + paymentInformation.getUserId() + " and for coin - " + paymentInformation.getSymbol());
            throw new RecallException("We try to create SPL Account many times but have some problem - " + exe.getMessage());
        }
    }
}
