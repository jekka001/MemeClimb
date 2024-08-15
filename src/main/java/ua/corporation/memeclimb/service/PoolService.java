package ua.corporation.memeclimb.service;

import ua.corporation.memeclimb.entity.main.Coin;
import ua.corporation.memeclimb.entity.main.Pool;
import ua.corporation.memeclimb.entity.main.dto.ParticipantDto;
import ua.corporation.memeclimb.entity.main.dto.PoolDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface PoolService {
    List<PoolDto> getPools(int page);

    PoolDto getPool(UUID uuid);

    ParticipantDto getParticipant(PoolDto pool, UserDto user);

    int getUserStep(UserDto user);

    void saveParticipant(UserDto user);

    void saveParticipant(ParticipantDto participant);

    void saveWinner(UserDto user);

    void save(Pool pool);

    boolean isPoolExist(int page);

    void addStep(UserDto user);

    Coin chosePoolRewardCoin(UUID uuid);
    Coin choseTopRewardCoin(UUID uuid);

    void delete();
}
