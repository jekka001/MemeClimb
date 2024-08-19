package ua.corporation.memeclimb.entity.main;

import lombok.Data;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;

import java.util.UUID;

@Data
public class PaymentInformation {
    private String receiverPublicKey;
    private String payerPublicKey;
    private byte[] payerPrivateKey;

    private UserDto user;
    private CoinDto coin;
    private int unitLimit;
    private int unitPrice;
    private double usdPrize;

    public static PaymentInformation getInstancePayForSpin(UserDto payer, CoinDto token, UserDto receiver,
                                                           int unitLimit, int unitPrice, UserDto user) {
        PaymentInformation paymentInformation = new PaymentInformation(
                payer.getPublicKey(),
                payer.getPrivateKey(),
                receiver.getPublicKey(),
                unitLimit,
                unitPrice,
                user
        );
        paymentInformation.setCoin(token);

        return paymentInformation;
    }

    public static PaymentInformation getInstanceSendPrize(UserDto payer, CoinDto token, UserDto receiver, double usdPrize,
                                                          int unitLimit, int unitPrice, UserDto user) {
        PaymentInformation paymentInformation = new PaymentInformation(
                payer.getPublicKey(),
                payer.getPrivateKey(),
                receiver.getPublicKey(),
                unitLimit,
                unitPrice,
                user
        );
        paymentInformation.setCoin(token);
        paymentInformation.setUsdPrize(usdPrize);

        return paymentInformation;
    }

    public static PaymentInformation getInstanceWithdraw(UserDto payer, String withdrawWallet, int unitLimit,
                                                         int unitPrice, UserDto user) {
        return new PaymentInformation(
                payer.getPublicKey(),
                payer.getPrivateKey(),
                withdrawWallet,
                unitLimit,
                unitPrice,
                user
        );
    }

    private PaymentInformation(String payerPublicKey, byte[] payerPrivateKey, String receiverPublicKey,
                               int unitLimit, int unitPrice, UserDto user) {
        this.payerPublicKey = payerPublicKey;
        this.payerPrivateKey = payerPrivateKey;
        this.receiverPublicKey = receiverPublicKey;
        this.unitLimit = unitLimit;
        this.unitPrice = unitPrice;
        this.user = user;
    }

    public UUID getUserId() {
        return user.getId();
    }

    public long getLamport() {
        return coin.getAmountRaw();
    }

    public String getSymbol() {
        return coin.getSymbol();
    }

    public double getAmount() {
        return coin.getAmount() != 0.0 ? coin.getAmount() : coin.getAmountByAmountRaw();
    }

    public long getAmountRaw() {
        return coin.getAmountRaw();
    }

    public String getMint() {
        return coin.getMint();
    }
}
