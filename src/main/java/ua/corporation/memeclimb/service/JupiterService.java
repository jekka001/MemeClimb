package ua.corporation.memeclimb.service;

import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.response.helpEntity.SwapResponse;

public interface JupiterService {

    SwapResponse getSwapTransaction(CoinDto coinDto, String serverPublicKey, double usdPrize, String associatedPublicKey);
}
