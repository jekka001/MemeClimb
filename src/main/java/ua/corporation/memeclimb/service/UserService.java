package ua.corporation.memeclimb.service;

import ua.corporation.memeclimb.entity.main.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto getServerWallet();
    UserDto create(com.pengrad.telegrambot.model.User telegramUser);

    List<UserDto> get();

    UserDto get(com.pengrad.telegrambot.model.User telegramUser);

    UserDto get(UUID userId);

    UserDto save(UserDto user);

    UserDto saveChosenPool(String poolId, UserDto user);

    void delete();
}
