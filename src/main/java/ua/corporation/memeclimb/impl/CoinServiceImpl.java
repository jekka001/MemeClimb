package ua.corporation.memeclimb.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.corporation.memeclimb.entity.main.Coin;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.mapper.CoinMapper;
import ua.corporation.memeclimb.repository.CoinRepository;
import ua.corporation.memeclimb.service.CoinService;
import ua.corporation.memeclimb.service.PoolService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoinServiceImpl implements CoinService {
    private final CoinRepository repository;
    private final CoinMapper mapper;
    private final PoolService poolService;

    @Override
    public List<CoinDto> getAll() {
        List<Coin> coins = repository.findAll();
        return mapper.toListDto(coins);
    }

    @Override
    public CoinDto getMainCoin() {
        Coin coin = repository.getCoinByMainIsTrue().orElse(getTestToken());

        return mapper.toDto(coin);
    }

    @Override
    public CoinDto getPoolRewardToken(UUID poolUuid) {
        Coin coin = poolService.chosePoolRewardCoin(poolUuid);

        return mapper.toDto(coin);
    }

    @Override
    public CoinDto getTopRewardToken(UUID poolUuid) {
        Coin coin = poolService.choseTopRewardCoin(poolUuid);

        return mapper.toDto(coin);
    }

    @Override
    public CoinDto getBySymbol(String symbol) {
        Coin coin = repository.findCoinBySymbol(symbol).orElseThrow(() -> new RuntimeException("We don't find coin by symbol"));

        return mapper.toDto(coin);
    }


    private Coin getTestToken() {
        Coin coin = new Coin();

        coin.setName("TEST");
        coin.setSymbol("TS");
        coin.setMint("TESTIC");
        coin.setMain(true);

        return coin;
    }

}
