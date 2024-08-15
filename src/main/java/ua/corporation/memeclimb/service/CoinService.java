package ua.corporation.memeclimb.service;

import ua.corporation.memeclimb.entity.main.dto.CoinDto;

import java.util.List;
import java.util.UUID;

public interface CoinService {
    CoinDto getBySymbol(String symbol);

    List<CoinDto> getAll();

    CoinDto getMainCoin();

    CoinDto getPoolRewardToken(UUID uuid);

    CoinDto getTopRewardToken(UUID uuid);
}
