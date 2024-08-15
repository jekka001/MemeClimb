package ua.corporation.memeclimb.service;

import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;

import java.util.Map;

public interface SolanaService {
    Map<byte[], String> createKeys(UserDto userDto);

    long getFee();

    void getPayFromUser(UserDto user, CoinDto coinDto, UserDto server);

    void sendPrize(UserDto user, CoinDto coinDto, UserDto server, double usdPrize);

    long getAccountBalance(UserDto userDto);

    void withdraw(UserDto user, String publicKey);
}
