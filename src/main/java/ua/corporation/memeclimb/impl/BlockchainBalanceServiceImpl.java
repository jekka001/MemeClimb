package ua.corporation.memeclimb.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ua.corporation.memeclimb.entity.main.PaymentInformation;
import ua.corporation.memeclimb.entity.main.SpinState;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.exception.EmptyBalanceException;
import ua.corporation.memeclimb.service.*;

@Service
@RequiredArgsConstructor
@Profile("Blockchain")
public class BlockchainBalanceServiceImpl implements BalanceService {
    @Value("${unit.limit}")
    private int unitLimit;
    @Value("${unit.price}")
    private int unitPrice;
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
        boolean isAllSteps = spinState.equals(SpinState.ALL_STEPS);

        PaymentInformation paymentInformation =
                isAllSteps ?
                        getPaymentForSendPoolReward(user, prize) :
                        getPaymentForSendTopReward(user, prize);

        solanaService.sendPrize(paymentInformation);

        return paymentInformation.getCoin();
    }

    private PaymentInformation getPaymentForSendPoolReward(UserDto user, double prize) {
        CoinDto mainCoin = coinService.getMainCoin();

        long fee = solanaService.getFee(unitLimit, unitPrice, mainCoin);
        mainCoin.setAmount(fee);
        double usdPrize = getUsdPrize(prize, mainCoin);
        CoinDto winToken = coinService.getPoolRewardToken(user.getChosenPoolId());

        return getPaymentForSendPrize(usdPrize, winToken, user);
    }

    private double getUsdPrize(double prize, CoinDto mainCoin) {
        double mainTokenUsdPrice = moralisService.getPriceForCoin(mainCoin);

        return mainTokenUsdPrice * (prize - mainCoin.getAmountByAmountRaw());
    }

    private PaymentInformation getPaymentForSendTopReward(UserDto user, double prize) {
        CoinDto winToken = coinService.getTopRewardToken(user.getChosenPoolId());

        return getPaymentForSendPrize(prize, winToken, user);
    }

    private PaymentInformation getPaymentForSendPrize(double usdPrize, CoinDto winToken, UserDto user) {
        UserDto server = userService.getServerWallet();

        double countOfToken = getCountOfToken(winToken, usdPrize);
        winToken.setAmount(countOfToken);

        return PaymentInformation.getInstanceSendPrize(server, winToken, user, usdPrize * MULTIPLY_6,
                unitLimit, unitPrice, user);
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

        PaymentInformation paymentInformation =
                PaymentInformation.getInstancePayForSpin(user, coinDto, server, unitLimit, unitPrice, user);

        solanaService.getPayFromUser(paymentInformation);
    }

    @Override
    public boolean checkBalance(UserDto user, double fee) {
        CoinDto mainCoin = coinService.getMainCoin();
        long feeTrans = solanaService.getFee(unitLimit, unitPrice, mainCoin);
        mainCoin.setAmountRaw(solanaService.getAccountBalance(user));

        double userHasMoney = mainCoin.getAmountByAmountRaw();

        if (userHasMoney < (fee + (feeTrans * 1.0f / mainCoin.getDecimalMultiplayer())) * 1.1) {
            throw new EmptyBalanceException("Balance empty");
        } else {
            return userHasMoney >= (fee + (feeTrans * 1.0f / mainCoin.getDecimalMultiplayer())) * 1.1;
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
        PaymentInformation paymentInformation =
                PaymentInformation.getInstanceWithdraw(user, withdrawWallet, 100_000, 1, user);

        solanaService.withdraw(paymentInformation);
    }
}
