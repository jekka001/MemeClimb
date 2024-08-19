package ua.corporation.memeclimb.impl.payment;

import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.types.config.Commitment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sol4k.Connection;
import org.sol4k.instruction.Instruction;
import ua.corporation.memeclimb.entity.main.PaymentInformation;
import ua.corporation.memeclimb.exception.PayForSpinException;
import ua.corporation.memeclimb.exception.RecallException;
import ua.corporation.memeclimb.impl.BasePayment;
import ua.corporation.memeclimb.impl.CheckOperation;
import ua.corporation.memeclimb.impl.PaymentOperation;
import ua.corporation.memeclimb.mapper.TransactionMapper;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class PayForUserSpin extends BasePayment implements PaymentOperation {
    private static final Logger logger = LoggerFactory.getLogger(PayForUserSpin.class);
    private final ReentrantLock lock;
    private final PaymentInformation paymentInformation;
    private final CheckOperation hashChecker;


    public PayForUserSpin(Connection sol4kConnection, RpcClient solanajClient, PaymentInformation paymentInformation,
                          ReentrantLock lock, TransactionMapper transactionMapper) {
        super(sol4kConnection, transactionMapper);
        this.paymentInformation = paymentInformation;
        this.lock = lock;
        this.hashChecker =
                new HashChecker(solanajClient, paymentInformation.getPayerPublicKey(), paymentInformation.getUserId(),
                        10, Commitment.FINALIZED);
    }

    public void run(int reCallCounter) {
        int counter = 0;
        try {
            logger.info("user - " + paymentInformation.getUserId() + " start pay " + paymentInformation.getSymbol() + " amount = " + paymentInformation.getAmount() + " for spin");
            lock.lock();
            payForSpin(counter, reCallCounter);
            lock.unlock();
            logger.info("user - " + paymentInformation.getUserId() + " finished pay " + paymentInformation.getSymbol() + " amount = " + paymentInformation.getAmount() + " for spin");
        } catch (Throwable exception) {
            lock.unlock();
            logger.error("user - " + paymentInformation.getUserId() + " can't pay " + paymentInformation.getSymbol() + " amount = " + paymentInformation.getAmount() + " for spin " + exception.getMessage());
            throw new PayForSpinException(exception);
        }
    }

    private void payForSpin(int counter, int reCallCounter) {
        try {
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

        Instruction transfer = createSol4kTransferInstruction(
                paymentInformation.getPayerPublicKey(),
                paymentInformation.getReceiverPublicKey(),
                paymentInformation.getLamport()
        );

        instructions.add(transfer);

        return instructions;
    }

    private void tryToFixProblem(int counter, int reCallCounter, Exception e) {

        if (isProblemWithBlockchain(e)) {
            try {
                Thread.sleep(5000);
                retryCall(counter, reCallCounter, e);
            } catch (InterruptedException ex) {
                logger.error("Server problem with pay for spin user - " + paymentInformation.getUserId());

                throw new RecallException("Some problem with Thread - " + ex);
            }
        } else {
            throw new PayForSpinException(e);
        }
    }

    private boolean isProblemWithBlockchain(Exception e) {
        return e.getMessage() != null && (
                e.getMessage().contains("Error processing Instruction") ||
                        e.getMessage().contains("Node is behind by") ||
                        e.getMessage().contains("Blockhash not found") ||
                        e.getMessage().contains("Server returned HTTP response code: 429") ||
                        e.getMessage().contains("We don't find this hash - ")
        );
    }

    private void retryCall(int counter, int reCallCounter, Exception e) {
        if (reCallCounter != counter) {
            counter++;
            logger.error("Blockchain problem with pay transaction " + " for user - " + paymentInformation.getUserId());
            payForSpin(counter, reCallCounter);
        } else {
            logger.error("Blockchain problem with pay transaction " + " for user - " + paymentInformation.getUserId());
            throw new RecallException("We retry many time but have some problem - " + e);
        }
    }
}
