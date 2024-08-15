package ua.corporation.memeclimb.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.corporation.memeclimb.entity.main.Coin;
import ua.corporation.memeclimb.entity.main.User;
import ua.corporation.memeclimb.entity.main.UserSplAddress;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.mapper.CoinMapper;
import ua.corporation.memeclimb.mapper.UserMapper;
import ua.corporation.memeclimb.repository.UserSplAddressRepository;
import ua.corporation.memeclimb.service.UserSplAddressService;

@Service
@RequiredArgsConstructor
public class UserSplAddressServiceImpl implements UserSplAddressService {
    private final UserSplAddressRepository repository;
    private final UserMapper userMapper;
    private final CoinMapper coinMapper;

    @Override
    public UserSplAddress getSplAddress(UserDto userDto, CoinDto coinDto) {
        User user = userMapper.toEntity(userDto);
        Coin coin = coinMapper.toEntity(coinDto);

        return repository.getUserSplAddressByUserAndCoin(user, coin);
    }

    @Override
    public UserSplAddress saveSplAddress(UserDto userDto, CoinDto coinDto, String address) {
        UserSplAddress userSplAddress = new UserSplAddress();

        userSplAddress.setUser(userMapper.toEntity(userDto));
        userSplAddress.setCoin(coinMapper.toEntity(coinDto));
        userSplAddress.setAssociatedTokenAddress(address);

        return repository.save(userSplAddress);
    }
}
