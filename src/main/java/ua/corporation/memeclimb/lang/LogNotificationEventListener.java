package ua.corporation.memeclimb.lang;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.ws.listeners.NotificationEventListener;

import java.util.logging.Logger;

public class LogNotificationEventListener implements NotificationEventListener {
    private static final Logger LOGGER = Logger.getLogger(LogNotificationEventListener.class.getName());
    private final RpcClient client;
    private PublicKey listeningPubkey;

    public LogNotificationEventListener(RpcClient client, PublicKey listeningPubkey) {
        this.client = client;
        this.listeningPubkey = listeningPubkey;
    }

    public void onNotificationEvent(Object data) {
        if (data != null) {
            LOGGER.info("completed");
        }
    }
}