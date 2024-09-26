package ua.corporation.memeclimb.impl.payment;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.SignatureInformation;
import org.p2p.solanaj.rpc.types.config.Commitment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sol4k.Connection;
import ua.corporation.memeclimb.exception.CheckHashException;
import ua.corporation.memeclimb.exception.EmptyBalanceException;
import ua.corporation.memeclimb.exception.ThreadException;
import ua.corporation.memeclimb.impl.CheckOperation;

import java.util.List;
import java.util.UUID;

public class SpinHashChecker implements CheckOperation {
    private static final Logger logger = LoggerFactory.getLogger(SpinHashChecker.class);

    private final RpcClient solanajClient;
    private final Connection sol4kConnection;
    private final String holderPublicKey;
    private final UUID userId;
    private final int reCheckCounter;
    private final Commitment commitment;

    private final long balanceBeforeSpin;

    public SpinHashChecker(Connection sol4kConnection, RpcClient solanajClient, String holderPublicKey, UUID userId, long balanceBeforeSpin, int reCheckCounter, Commitment commitment) {
        this.solanajClient = solanajClient;
        this.sol4kConnection = sol4kConnection;
        this.holderPublicKey = holderPublicKey;
        this.userId = userId;
        this.reCheckCounter = reCheckCounter;
        this.commitment = commitment;
        this.balanceBeforeSpin = balanceBeforeSpin;
    }

    @Override
    public void check(String hash) throws RpcException {
        int counter = 0;
        checkHash(hash, counter);
    }

    private void checkHash(String hash, int counter) throws RpcException {
        PublicKey userPublicKey = new PublicKey(holderPublicKey);
        List<SignatureInformation> signs =
                solanajClient.getApi().getSignaturesForAddress(userPublicKey, 5, commitment);
//        long balanceAfterSpin = getAccountBalance();
        for (SignatureInformation signatureInformation : signs) {
            if (signatureInformation.getSignature().equals(hash)) {
                if (signatureInformation.getErr() == null) {
                    return;
                } else {
                    throw new EmptyBalanceException("99 % user spin when don't have money:) ");
                }
            }
        }

        retryCheck(hash, counter);
    }

    private void retryCheck(String hash, int counter) throws RpcException {
        if (reCheckCounter != counter) {
            tryCheckAgain(hash, counter);
        } else {
            logger.error("Blockchain problem with check hash - " + hash + " for user - " + userId);
            throw new CheckHashException("We don't find this hash - " + hash);
        }
    }

    private void tryCheckAgain(String hash, int counter) throws RpcException {
        try {
            Thread.sleep(1000L * (counter + 1));
            counter++;
            logger.info("Try to check hash - " + hash + " for user - " + userId);
            checkHash(hash, counter);
        } catch (InterruptedException e) {
            logger.error("Server problem with check transaction hash - " + hash + " for user - " + userId);
            throw new ThreadException("Some problem with Thread sleep in hashChecker - " + e.getMessage());
        }
    }

//    private long getAccountBalance() {
//        AccountInfo accountInfo = sol4kConnection.getAccountInfo(new org.sol4k.PublicKey(holderPublicKey));
//
//        if (accountInfo != null) {
//            return accountInfo.getLamports().longValue();
//        }
//
//        return 0;
//    }
}
