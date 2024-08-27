package ua.corporation.memeclimb.impl;

import org.p2p.solanaj.rpc.RpcClient;
import org.sol4k.Connection;
import org.sol4k.PublicKey;
import org.sol4k.api.AccountInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.corporation.memeclimb.entity.main.PaymentInformation;
import ua.corporation.memeclimb.entity.main.UserSplAddress;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.impl.payment.CreateSPLTokenAddressForUser;
import ua.corporation.memeclimb.impl.payment.PayForUserSpin;
import ua.corporation.memeclimb.impl.payment.SendPrizeToUser;
import ua.corporation.memeclimb.impl.payment.WithdrawUserCoins;
import ua.corporation.memeclimb.mapper.TransactionMapper;
import ua.corporation.memeclimb.service.*;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SolanaServiceImpl implements SolanaService {
    private final int feeInSolana;
    private final RpcClient solanajClient;
    private final Connection sol4kConnection;
    private final JupiterService jupiterService;
    private final UserSplAddressService splAddressService;
    private final MoralisService moralisService;
    private final CoinService coinService;
    private final TransactionMapper transactionMapper;

    public SolanaServiceImpl(@Value("${helius.rpc.client}") String heliusRPCClient, @Value("${fee.in.solana}") int feeInSolana,
                             JupiterService jupiterService, UserSplAddressService splAddressService,
                             MoralisService moralisService, CoinService coinService, TransactionMapper transactionMapper) {
        this.solanajClient = new RpcClient(heliusRPCClient);
        this.sol4kConnection = new Connection(heliusRPCClient);
        this.feeInSolana = feeInSolana;
        this.jupiterService = jupiterService;
        this.splAddressService = splAddressService;
        this.moralisService = moralisService;
        this.coinService = coinService;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public long getFee(int unitLimit, int unitPrice, CoinDto mainCoin) {
        int unitToLamport = 1000;

        return ((long) unitLimit * unitPrice * unitToLamport) / mainCoin.getDecimalMultiplayer() + feeInSolana;
    }

    @Override
    @Transactional
    public void getPayFromUser(PaymentInformation paymentInformation) {
        ReentrantLock lock = new ReentrantLock();

        PayForUserSpin payForUserSpin =
                new PayForUserSpin(sol4kConnection, solanajClient, paymentInformation, lock, transactionMapper);

        payForUserSpin.run(3);
    }

    @Override
    @Transactional
    public void sendPrize(PaymentInformation paymentInformation) {
        ReentrantLock lock = new ReentrantLock();

        if (!paymentInformation.getCoin().getSymbol().equals("USDT")) {
            sendPrizeTransactionForAllToken(paymentInformation, lock);
        } else {
            sendPrizeTransactionForUSDT(paymentInformation, lock);
        }
    }

    private void sendPrizeTransactionForAllToken(PaymentInformation paymentInformation, ReentrantLock lock) {
        String associatedAddress = getAssociatedAddress(paymentInformation, lock);

        SendPrizeToUser sendPrizeToUser = new SendPrizeToUser(sol4kConnection, solanajClient, jupiterService,
                transactionMapper, paymentInformation, lock, associatedAddress);
        sendPrizeToUser.run();
    }

    private String getAssociatedAddress(PaymentInformation paymentInformation, ReentrantLock lock) {
        UserSplAddress userSplAddress =
                splAddressService.getSplAddress(paymentInformation.getUser(), paymentInformation.getCoin());

        return userSplAddress != null ?
                userSplAddress.getAssociatedTokenAddress() :
                createAndSaveSplAddress(paymentInformation, lock);
    }

    private String createAndSaveSplAddress(PaymentInformation paymentInformation, ReentrantLock lock) {
        String associatedAddress = getAssociatedAddressForWallet(paymentInformation, lock);
        splAddressService.saveSplAddress(paymentInformation.getUser(), paymentInformation.getCoin(), associatedAddress);

        return associatedAddress;
    }

    private void sendPrizeTransactionForUSDT(PaymentInformation paymentInformation, ReentrantLock lock) {
        paymentInformation.getCoin().setAmountRaw(Double.valueOf(paymentInformation.getUsdPrize()).longValue());

        String associatedAddressForWallet = getAssociatedAddressForWallet(paymentInformation, lock);

        WithdrawUserCoins sendUSDTPrize = new WithdrawUserCoins(sol4kConnection, solanajClient, lock,
                transactionMapper, paymentInformation, splAddressService, associatedAddressForWallet);

        sendUSDTPrize.run(3);
    }

    @Override
    public long getAccountBalance(UserDto userDto) {
        AccountInfo accountInfo = sol4kConnection.getAccountInfo(new PublicKey(userDto.getPublicKey()));

        if (accountInfo != null) {
            return accountInfo.getLamports().longValue();
        }

        return 0;
    }

    @Override
    public void withdraw(PaymentInformation paymentInformation) {
        ReentrantLock lock = new ReentrantLock();

        sendAllSPLTokenTransactions(paymentInformation, lock);
        sendMainTokenTransaction(paymentInformation, lock);
    }

    private void sendAllSPLTokenTransactions(PaymentInformation paymentInformation, ReentrantLock lock) {
        List<CoinDto> coins = moralisService.getSPLToken(paymentInformation.getUser());
        for (CoinDto splToken : coins) {
            sendSplTokenTransaction(paymentInformation, lock, splToken);
        }
    }

    private void sendSplTokenTransaction(PaymentInformation paymentInformation, ReentrantLock lock, CoinDto splToken) {
        setCoinAmountRaw(paymentInformation, splToken);
        String associatedAddressForWallet = getAssociatedAddressForWallet(paymentInformation, lock);

        WithdrawUserCoins withdrawUserCoins = new WithdrawUserCoins(sol4kConnection, solanajClient, lock,
                transactionMapper, paymentInformation, splAddressService, associatedAddressForWallet);

        withdrawUserCoins.run(3);
    }

    private void setCoinAmountRaw(PaymentInformation paymentInformation, CoinDto splToken) {
        CoinDto coinInDB = coinService.getBySymbol(splToken.getSymbol());
        coinInDB.setAmountRaw(splToken.getAmountRaw());

        paymentInformation.setCoin(coinInDB);
    }

    private void sendMainTokenTransaction(PaymentInformation paymentInformation, ReentrantLock lock) {
        setCoinAmountRaw(paymentInformation);

        PayForUserSpin makeTransactionRunnable =
                new PayForUserSpin(sol4kConnection, solanajClient, paymentInformation, lock, transactionMapper);

        makeTransactionRunnable.run(3);
    }

    private void setCoinAmountRaw(PaymentInformation paymentInformation) {
        CoinDto mainCoin = coinService.getMainCoin();
        long lamport = getAccountBalance(paymentInformation.getUser());
        mainCoin.setAmountRaw((lamport - (getFee(paymentInformation.getUnitLimit(), paymentInformation.getUnitPrice(), mainCoin) + 990880)));

        paymentInformation.setCoin(mainCoin);
    }

    private String getAssociatedAddressForWallet(PaymentInformation paymentInformation, ReentrantLock lock) {
        CreateSPLTokenAddressForUser createUserSplTokenAddress =
                new CreateSPLTokenAddressForUser(sol4kConnection, solanajClient,
                        transactionMapper, paymentInformation, lock);

        return createUserSplTokenAddress.run();
    }

//    @Override
//    public long getFeeForUserSendPrize() {
//        try {
//            return getFees("JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4").getPriorityFeeEstimate().longValue();
//        } catch (RpcException e) {
//            throw new ServerProblemException(e);
//        }
//    }

    //    private Fee getFees(String publicKey) throws RpcException {
//        List<Object> params = new ArrayList();
//        if (null != publicKey) {
//            params.add(Map.of("accountKeys", List.of(publicKey)));
//            params.add(Map.of("options", List.of("recommended", true)));
//        }
//
//        return this.solanajClient.call("getPriorityFeeEstimate", params, Fee.class);
//    }

}
