package ua.corporation.memeclimb.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ua.corporation.memeclimb.entity.main.SpinState;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.exception.EmptyBalanceException;
import ua.corporation.memeclimb.service.*;

@Service
@RequiredArgsConstructor
@Profile("Blockchain")
public class BlockchainBalanceServiceImpl implements BalanceService {
    public static final Integer MULTIPLY_6 = 1_000_000;
    private static final double AROUND = 0.99;
    private final MoralisService moralisService;
    private final SolanaService solanaService;
    private final UserService userService;
    private final CoinService coinService;

    @Override
    public String showBalance(UserDto user) {
        return moralisService.getAllTokenBalance(user);
    }

    @Override
    public CoinDto addPrize(UserDto user, double prize, SpinState spinState) {
        double usdPrize;
        CoinDto winToken;
        long fee = solanaService.getFee();
        UserDto server = userService.getServerWallet();
        if (spinState.equals(SpinState.ALL_STEP)) {
            winToken = coinService.getPoolRewardToken(user.getChosenPoolId());
            CoinDto mainCoin = coinService.getMainCoin();
            mainCoin.setAmountRaw(fee);

            usdPrize = getUsdPrize(prize, mainCoin);
        } else {
            usdPrize = prize;
            winToken = coinService.getTopRewardToken(user.getChosenPoolId());
        }
        double countOfToken = getCountOfToken(winToken, usdPrize);
        winToken.setAmount(countOfToken);

        solanaService.sendPrize(user, winToken, server, usdPrize * MULTIPLY_6);

        return winToken;
    }

    private double getUsdPrize(double prize, CoinDto mainCoin) {
        double mainTokenUsdPrice = moralisService.getPriceForCoin(mainCoin);

        return mainTokenUsdPrice * (prize - mainCoin.getAmountByAmountRaw());
    }

    private double getCountOfToken(CoinDto winToken, double usdPrize) {
        double winTokenUsdPrice = moralisService.getPriceForCoin(winToken);

        return (usdPrize / winTokenUsdPrice) * AROUND;
    }

    @Override
    public void subtractFee(UserDto user, double fee) {
        CoinDto coinDto = coinService.getMainCoin();
        coinDto.setAmountRawByAmount(fee);
        UserDto server = userService.getServerWallet();

        solanaService.getPayFromUser(user, coinDto, server);
    }

    @Override
    public boolean checkBalance(UserDto user, double fee, long chatId) {
        CoinDto mainCoin = coinService.getMainCoin();
        long feeTrans = (1400000 * 100000L) * 10000 / mainCoin.getDecimalMultiplayer();
        mainCoin.setAmountRaw(solanaService.getAccountBalance(user));

        double userHasMoney = mainCoin.getAmountByAmountRaw();
        if (userHasMoney < (fee + (feeTrans * 1.0f / mainCoin.getDecimalMultiplayer()))) {
            throw new EmptyBalanceException("Balance empty", chatId);
        } else {
            return userHasMoney >= (fee + (feeTrans * 1.0f / mainCoin.getDecimalMultiplayer()));
        }
    }

    @Override
    public void sendMoneyToUser(UserDto user) {

    }

    @Override
    public void addVirtualMoney(UserDto user, double amount) {

    }

    @Override
    public void withdraw(UserDto user, String withdrawWallet) {
        solanaService.withdraw(user, withdrawWallet);
    }
}
