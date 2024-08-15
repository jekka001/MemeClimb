package ua.corporation.memeclimb.impl;

import org.p2p.solanaj.rpc.RpcException;

public interface CheckOperation {
    void check(String hash) throws RpcException;
}
