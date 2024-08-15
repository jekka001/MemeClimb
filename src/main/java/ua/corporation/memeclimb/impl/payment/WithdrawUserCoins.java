package ua.corporation.memeclimb.impl.payment;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.config.Commitment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sol4k.Connection;
import org.sol4k.instruction.Instruction;
import ua.corporation.memeclimb.entity.main.PaymentInformation;
import ua.corporation.memeclimb.entity.main.UserSplAddress;
import ua.corporation.memeclimb.exception.RecallException;
import ua.corporation.memeclimb.exception.WithdrawException;
import ua.corporation.memeclimb.impl.BasePayment;
import ua.corporation.memeclimb.impl.CheckOperation;
import ua.corporation.memeclimb.impl.PaymentOperation;
import ua.corporation.memeclimb.mapper.TransactionMapper;
import ua.corporation.memeclimb.service.UserSplAddressService;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class WithdrawUserCoins extends BasePayment implements PaymentOperation {
    private static final Logger logger = LoggerFactory.getLogger(WithdrawUserCoins.class);
    private final RpcClient solanajClient;
    private final Connection sol4kConnection;
    private final ReentrantLock lock;
    private final TransactionMapper transactionMapper;
    private final PaymentInformation paymentInformation;
    private final UserSplAddressService splAddressService;
    private final CheckOperation hashChecker;

    private final String walletAssociatedAddress;
    private String splUserServerAddress;


    public WithdrawUserCoins(Connection sol4kConnection, RpcClient solanajClient, ReentrantLock lock,
                             TransactionMapper transactionMapper, PaymentInformation paymentInformation,
                             UserSplAddressService splAddressService, String walletAssociatedAddress) {
        super(sol4kConnection, transactionMapper);
        this.sol4kConnection = sol4kConnection;
        this.solanajClient = solanajClient;
        this.lock = lock;
        this.transactionMapper = transactionMapper;
        this.paymentInformation = paymentInformation;
        this.splAddressService = splAddressService;
        this.walletAssociatedAddress = walletAssociatedAddress;
        this.hashChecker = new HashChecker(solanajClient, paymentInformation.getPayerPublicKey(), paymentInformation.getUserId(),
                10, Commitment.FINALIZED);
    }

    public void run(int reCallCounter) {
        try {
            logger.info("user - " + paymentInformation.getUserId() + " start withdraw ");
            lock.lock();
            int counter = 0;
            withdraw(counter, reCallCounter);
            lock.unlock();
            logger.info("user - " + paymentInformation.getUserId() + " finished withdraw ");
        } catch (Throwable exception) {
            lock.unlock();
            logger.error("user - " + paymentInformation.getUserId() + " withdraw error");
            throw new WithdrawException(exception);
        }
    }

    private void withdraw(int counter, int reCallCounter) {
        try {
            splUserServerAddress = getSplTokenForUserServerWallet();
            String hash = sendPaymentTransaction(paymentInformation);
            Thread.sleep(5000);
            hashChecker.check(hash);
        } catch (Exception e) {
            tryToFixProblem(counter, reCallCounter, e);
        }
    }

    @Override
    protected List<Instruction> getPaymentInstructions(PaymentInformation paymentInformation) {
        List<Instruction> instructions =
                getComputerBudgetInstructions(paymentInformation.getUnitLimit(), paymentInformation.getUnitPrice());

        Instruction transfer = createSol4kSPLTransferInstruction(
                splUserServerAddress,
                walletAssociatedAddress,
                paymentInformation.getPayerPublicKey(),
                Double.valueOf(paymentInformation.getAmountRaw()).longValue()
        );

        instructions.add(transfer);

        return instructions;
    }

    private void tryToFixProblem(int counter, int reCallCounter, Exception e) {
        if (isProblemWithBlockchain(e)) {
            retryCall(counter, reCallCounter, e);
        } else {
            throw new WithdrawException(e);
        }
    }

    private String getSplTokenForUserServerWallet() throws RpcException {
        UserSplAddress userSplAddress =
                splAddressService.getSplAddress(paymentInformation.getUser(), paymentInformation.getCoin());

        return userSplAddress != null ? userSplAddress.getAssociatedTokenAddress() : solanajClient.getApi().getTokenAccountsByOwner(
                new PublicKey(paymentInformation.getPayerPublicKey()),
                new PublicKey(paymentInformation.getMint())).toString();
    }

    private boolean isProblemWithBlockchain(Exception e) {
        return e.getMessage() != null && (
                e.getMessage().contains("Error processing Instruction") ||
                        e.getMessage().contains("Node is behind by") ||
                        e.getMessage().contains("Blockhash not found") ||
                        e.getMessage().contains("Server returned HTTP response code: 429") ||
                        e.getMessage().contains("We don't find this hash")
        );
    }

    private void retryCall(int counter, int reCallCounter, Exception e) {
        if (reCallCounter != counter) {
            counter++;
            logger.error("Blockchain problem with pay transaction " + " for user - " + paymentInformation.getUserId());
            withdraw(counter, reCallCounter);
        } else {
            logger.error("Blockchain problem with pay transaction " + " for user - " + paymentInformation.getUserId());
            throw new RecallException("We retry many time but have some problem - " + e);
        }
    }

}
