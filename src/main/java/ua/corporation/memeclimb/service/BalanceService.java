package ua.corporation.memeclimb.service;

import ua.corporation.memeclimb.entity.main.SpinState;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;

public interface BalanceService {
    String showBalance(UserDto user);

    CoinDto addPrize(UserDto user, double prize, SpinState spinState);

    void subtractFee(UserDto user, double fee);

    boolean checkBalance(UserDto user, double fee);

    void sendMoneyToUser(UserDto user);

    void addVirtualMoney(UserDto user, double money);

    void withdraw(UserDto user, String withdrawWallet);
}
