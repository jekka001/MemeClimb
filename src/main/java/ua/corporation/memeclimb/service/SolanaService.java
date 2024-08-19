package ua.corporation.memeclimb.service;

import ua.corporation.memeclimb.entity.main.PaymentInformation;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;

import java.util.Map;

public interface SolanaService {
    long getFee(int unitLimit, int unitPrice, CoinDto mainCoin);

    void getPayFromUser(PaymentInformation paymentInformation);

    void sendPrize(PaymentInformation paymentInformation);

    long getAccountBalance(UserDto userDto);

    void withdraw(PaymentInformation paymentInformation);
}
