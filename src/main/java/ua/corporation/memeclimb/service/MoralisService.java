package ua.corporation.memeclimb.service;

import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;

import java.util.List;

public interface MoralisService {

    String getAllTokenBalance(UserDto userDto);

    long getMainTokenBalance(UserDto userDto);

    List<CoinDto> getSPLToken(UserDto userDto);

    double getPriceForCoin(CoinDto coinDto);

}
