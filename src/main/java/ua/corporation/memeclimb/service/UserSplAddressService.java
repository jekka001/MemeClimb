package ua.corporation.memeclimb.service;

import ua.corporation.memeclimb.entity.main.UserSplAddress;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;

public interface UserSplAddressService {
    UserSplAddress getSplAddress(UserDto userDto, CoinDto coinDto);

    UserSplAddress saveSplAddress(UserDto userDto, CoinDto coinDto, String address);

}
