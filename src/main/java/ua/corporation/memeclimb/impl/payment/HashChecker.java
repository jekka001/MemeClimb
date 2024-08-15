package ua.corporation.memeclimb.impl.payment;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.SignatureInformation;
import org.p2p.solanaj.rpc.types.config.Commitment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.corporation.memeclimb.exception.CheckHashException;
import ua.corporation.memeclimb.exception.ThreadException;
import ua.corporation.memeclimb.impl.CheckOperation;

import java.util.List;
import java.util.UUID;

public class HashChecker implements CheckOperation {
    private static final Logger logger = LoggerFactory.getLogger(HashChecker.class);

    private final RpcClient solanajClient;
    private final String holderPublicKey;
    private final UUID userId;
    private final int reCheckCounter;
    private final Commitment commitment;

    public HashChecker(RpcClient solanajClient, String holderPublicKey, UUID userId, int reCheckCounter, Commitment commitment) {
        this.solanajClient = solanajClient;
        this.holderPublicKey = holderPublicKey;
        this.userId = userId;
        this.reCheckCounter = reCheckCounter;
        this.commitment = commitment;
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

        for (SignatureInformation signatureInformation : signs) {
            if (signatureInformation.getSignature().equals(hash)) {
                return;
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
}
