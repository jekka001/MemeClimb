package ua.corporation.memeclimb.impl;

import lombok.RequiredArgsConstructor;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.sol4k.Connection;
import org.sol4k.PublicKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.corporation.memeclimb.entity.main.PaymentInformation;
import ua.corporation.memeclimb.entity.main.UserSplAddress;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.entity.main.response.helpEntity.Fee;
import ua.corporation.memeclimb.impl.payment.CreateSPLTokenAddressForUser;
import ua.corporation.memeclimb.impl.payment.PayForUserSpin;
import ua.corporation.memeclimb.impl.payment.SendPrizeToUser;
import ua.corporation.memeclimb.impl.payment.WithdrawUserCoins;
import ua.corporation.memeclimb.mapper.TransactionMapper;
import ua.corporation.memeclimb.service.*;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class SolanaServiceImpl implements SolanaService {
    private final JupiterService jupiterService;
    private final RpcClient solanajClient = new RpcClient("https://mainnet.helius-rpc.com/?api-key=d104bff9-29c0-4494-b356-56a9a0ea4e3e");
    private final Connection sol4kConnection = new Connection("https://mainnet.helius-rpc.com/?api-key=d104bff9-29c0-4494-b356-56a9a0ea4e3e");
    private final UserSplAddressService splAddressService;
    private final TransactionMapper transactionMapper;
    private final MoralisService moralisService;
    private final CoinService coinService;
    private final Random random = new Random();


    private ReentrantLock lock = new ReentrantLock();

    @Override
    public Map<byte[], String> createKeys(UserDto user) {
        Map<byte[], String> keys = new HashMap<>(1);

        Account account = createAccount(user);
        keys.put(account.getSecretKey(), account.getPublicKey().toString());

        return keys;
    }

    @Override
    public long getFee() {
        try {
            return getFees("JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4").getPriorityFeeEstimate().longValue();
        } catch (RpcException e) {
            throw new RuntimeException(e);
        }
    }

    public Fee getFees(String publicKey) throws RpcException {
        List<Object> params = new ArrayList();
        if (null != publicKey) {
            params.add(Map.of("accountKeys", List.of(publicKey)));
            params.add(Map.of("options", List.of("recommended", true)));
        }

        return this.solanajClient.call("getPriorityFeeEstimate", params, Fee.class);
    }

    private Account createAccount(UserDto user) {
        List<String> words = new ArrayList<>();

        words.add(user.getName());
        words.add(user.getTelegramId());
        words.add(generateRandomPhrase());

        return Account.fromBip44Mnemonic(words, generateRandomPhrase());
    }

    private String generateRandomPhrase() {
        StringBuilder stringBuilder = new StringBuilder();
        String phrase = "memeClimb";

        for (int counter = 0; counter < phrase.length(); counter++) {
            int randomNumber = random.nextInt(phrase.length());
            stringBuilder.append(phrase.charAt(randomNumber));
        }

        return stringBuilder.toString();
    }

    @Override
    @Transactional
    public void getPayFromUser(UserDto user, CoinDto coin, UserDto server) {
        PaymentInformation paymentInformation =
                PaymentInformation.getInstancePayForSpin(user, coin, server, 1400000, 100000, user);

        PayForUserSpin makeTransactionRunnable =
                new PayForUserSpin(sol4kConnection, solanajClient, paymentInformation, lock, transactionMapper);

        makeTransactionRunnable.run(3);
    }

    @Override
    @Transactional
    public void sendPrize(UserDto user, CoinDto coin, UserDto server, double usdPrize) {
        if (!coin.getSymbol().equals("USDT")) {
            PaymentInformation paymentInformation =
                    PaymentInformation.getInstanceSendPrize(server, coin, user, usdPrize, 1400000, 100000, user);
            String associatedAddress = getAssociatedAddress(paymentInformation);

            SendPrizeToUser sendPrizeToUser = new SendPrizeToUser(sol4kConnection, solanajClient, jupiterService,
                    transactionMapper, paymentInformation, lock, associatedAddress);

            sendPrizeToUser.run();
        } else {
            coin.setAmountRaw(Double.valueOf(usdPrize).longValue());
            PaymentInformation paymentInformation =
                    PaymentInformation.getInstanceWithdraw(server, coin, user.getPublicKey(), 1000000, 100000, user);

            String associatedAddressForWallet = getAssociatedAddressForWallet(paymentInformation);

            WithdrawUserCoins withdrawUserCoins = new WithdrawUserCoins(sol4kConnection, solanajClient, lock,
                    transactionMapper, paymentInformation, splAddressService, associatedAddressForWallet);

            withdrawUserCoins.run(3);
        }
    }

    private String getAssociatedAddress(PaymentInformation paymentInformation) {
        UserSplAddress userSplAddress =
                splAddressService.getSplAddress(paymentInformation.getUser(), paymentInformation.getCoin());

        if (userSplAddress == null) {
            CreateSPLTokenAddressForUser createUserSplTokenAddress =
                    new CreateSPLTokenAddressForUser(sol4kConnection, solanajClient,
                            transactionMapper, paymentInformation, lock);
            String associatedAddress = createUserSplTokenAddress.run();
            splAddressService.saveSplAddress(paymentInformation.getUser(), paymentInformation.getCoin(), associatedAddress);

            return associatedAddress;
        }

        return userSplAddress.getAssociatedTokenAddress();
    }

    @Override
    public long getAccountBalance(UserDto userDto) {
        return sol4kConnection.getAccountInfo(new PublicKey(userDto.getPublicKey())).getLamports().longValue();
    }

    @Override
    public void withdraw(UserDto user, String userWallet) {
        List<CoinDto> coins = moralisService.getSPLToken(user);
        for (CoinDto splToken : coins) {
            CoinDto coinInDB = coinService.getBySymbol(splToken.getSymbol());
            coinInDB.setAmountRaw(splToken.getAmountRaw());

            PaymentInformation paymentInformation =
                    PaymentInformation.getInstanceWithdraw(user, coinInDB, userWallet, 1000000, 100000, user);

            String associatedAddressForWallet = getAssociatedAddressForWallet(paymentInformation);

            WithdrawUserCoins withdrawUserCoins = new WithdrawUserCoins(sol4kConnection, solanajClient, lock,
                    transactionMapper, paymentInformation, splAddressService, associatedAddressForWallet);

            withdrawUserCoins.run(3);
        }
        CoinDto coinDto = coinService.getMainCoin();
        long lamport = getAccountBalance(user);
        coinDto.setAmountRaw((lamport - (1400000 * 100000L) * 10000 / coinDto.getDecimalMultiplayer()));
        UserDto realUserWallet = new UserDto("userWallet", "userWallet");
        realUserWallet.setId(UUID.randomUUID());
        realUserWallet.setPublicKey(userWallet);

        PaymentInformation paymentInformation =
                PaymentInformation.getInstancePayForSpin(user, coinDto, realUserWallet, 1400000, 100000, user);

        PayForUserSpin makeTransactionRunnable =
                new PayForUserSpin(sol4kConnection, solanajClient, paymentInformation, lock, transactionMapper);

        makeTransactionRunnable.run(3);
    }

    private String getAssociatedAddressForWallet(PaymentInformation paymentInformation) {
        CreateSPLTokenAddressForUser createUserSplTokenAddress =
                new CreateSPLTokenAddressForUser(sol4kConnection, solanajClient,
                        transactionMapper, paymentInformation, lock);

        return createUserSplTokenAddress.run();
    }
}
