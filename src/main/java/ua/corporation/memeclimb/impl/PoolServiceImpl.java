package ua.corporation.memeclimb.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.corporation.memeclimb.entity.main.Coin;
import ua.corporation.memeclimb.entity.main.Pool;
import ua.corporation.memeclimb.entity.main.PoolCoin;
import ua.corporation.memeclimb.entity.main.User;
import ua.corporation.memeclimb.entity.main.dto.ParticipantDto;
import ua.corporation.memeclimb.entity.main.dto.PoolDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.mapper.PoolMapper;
import ua.corporation.memeclimb.mapper.UserMapper;
import ua.corporation.memeclimb.repository.PoolRepository;
import ua.corporation.memeclimb.service.ParticipantService;
import ua.corporation.memeclimb.service.PoolService;
import ua.corporation.memeclimb.service.WinnerService;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PoolServiceImpl implements PoolService {
    private final PoolRepository repository;
    private final ParticipantService participantService;
    private final WinnerService winnerService;
    private final PoolMapper mapper;
    private final UserMapper userMapper;
    private final Random random = new Random();


    @Override
    public List<PoolDto> getPools(int page) {
        Pageable pageable = PageRequest.of(page, 3);

        List<Pool> pools = repository.getPools(pageable).get()
                .collect(Collectors.toList());

        return mapper.toListDto(pools).stream().sorted().collect(Collectors.toList());
    }

    @Override
    public PoolDto getPool(UUID uuid) {
        Pool pool = repository.findById(uuid).orElseThrow(() -> new RuntimeException("Pool not founded"));
        return mapper.toDto(pool);
    }

    @Override
    public ParticipantDto getParticipant(PoolDto pool, UserDto user) {
        return participantService.get(pool, user);
    }

    @Override
    public int getUserStep(UserDto user) {
        if (user.getChosenPoolId() == null) {
            return 0;
        }
        PoolDto chosenPool = getPool(user.getChosenPoolId());

        return participantService.getUserStep(chosenPool, user);
    }

    @Override
    public void saveParticipant(UserDto user) {
        PoolDto chosenPool = getPool(user.getChosenPoolId());

        participantService.save(chosenPool, user);
    }

    @Override
    public void saveParticipant(ParticipantDto participantDto) {
        participantService.save(participantDto);
    }

    @Override
    public void saveWinner(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        PoolDto chosenPool = getPool(user.getChosenPoolId());
        Pool pool = mapper.toEntity(chosenPool);
        winnerService.save(pool, user);
    }

    @Override
    public void save(Pool pool) {
        repository.saveAndFlush(pool);
    }

    @Override
    public boolean isPoolExist(int page) {
        return getPools(page).size() != 0;
    }

    @Override
    public void addStep(UserDto user) {
        PoolDto pool = getPool(user.getChosenPoolId());

        participantService.changeStep(pool, user);
    }

    @Override
    public Coin chosePoolRewardCoin(UUID uuid) {
        PoolDto pool = getPool(uuid);

        List<Coin> coins = pool.getPoolCoins().stream().map(PoolCoin::getCoin).toList();
        int randomNumber = random.nextInt(coins.size());

        return coins.get(randomNumber);
    }

    @Override
    public Coin choseTopRewardCoin(UUID uuid) {
        PoolDto pool = getPool(uuid);

        PoolCoin poolCoin = pool.getTopReward();

        return poolCoin.getCoin();
    }

    @Override
    public void delete() {
        repository.deleteAll();
    }
}
