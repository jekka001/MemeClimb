package ua.corporation.memeclimb.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.entity.main.response.helpEntity.AccountBalance;
import ua.corporation.memeclimb.entity.main.response.helpEntity.NativeBalance;
import ua.corporation.memeclimb.entity.main.response.helpEntity.TokenPrice;
import ua.corporation.memeclimb.service.MoralisService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class MoralisServiceImpl implements MoralisService {

    @Value("${moralis.account.url}")
    private String moralisAccountUrl;

    @Value("${moralis.token.url}")
    private String moralisTokenUrl;

    @Value("${moralis.api.key}")
    private String moralisApiKey;

    @Override
    public String getAllTokenBalance(UserDto userDto) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = moralisAccountUrl + userDto.getPublicKey() + "/portfolio";

        HttpEntity<String> entity = prepareEntity();

        return prepareResponse(restTemplate, requestUrl, entity);
    }

    @Override
    public long getMainTokenBalance(UserDto userDto) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = moralisAccountUrl + userDto.getPublicKey() + "/portfolio";

        HttpEntity<String> entity = prepareEntity();

        return getLamports(restTemplate, requestUrl, entity);
    }

    @Override
    public List<CoinDto> getSPLToken(UserDto userDto) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = moralisAccountUrl + userDto.getPublicKey() + "/portfolio";

        HttpEntity<String> entity = prepareEntity();

        return prepareSPLTokens(restTemplate, requestUrl, entity);
    }

    @Override
    public double getPriceForCoin(CoinDto coinDto) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = moralisTokenUrl + coinDto.getMint() + "/price";

        HttpEntity<String> entity = prepareEntity();

        return getPrice(restTemplate, requestUrl, entity);
    }

    private HttpEntity<String> prepareEntity() {
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("X-API-Key", moralisApiKey);

        return new HttpEntity<>("body", headers);
    }

    private String prepareResponse(RestTemplate restTemplate, String requestUrl, HttpEntity<String> entity) {
        ResponseEntity<AccountBalance> responseBody = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, AccountBalance.class);

        List<CoinDto> coins = Objects.requireNonNull(responseBody.getBody()).getCoins();
        NativeBalance nativeBalance = responseBody.getBody().getNativeBalance();

        return getBalance(coins, nativeBalance);
    }

    public String getBalance(List<CoinDto> coins, NativeBalance nativeBalance) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n").append(nativeBalance.getSolana()).append(" ").append("SOL");
        coins.forEach(coin -> builder.append("\n").append(coin.getAmount()).append(" ").append(coin.getSymbol()));

        return builder.toString();
    }

    private List<CoinDto> prepareSPLTokens(RestTemplate restTemplate, String requestUrl, HttpEntity<String> entity) {
        ResponseEntity<AccountBalance> responseBody = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, AccountBalance.class);

        return Objects.requireNonNull(responseBody.getBody()).getCoins();
    }

    private long getLamports(RestTemplate restTemplate, String requestUrl, HttpEntity<String> entity) {
        ResponseEntity<AccountBalance> responseBody = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, AccountBalance.class);

        NativeBalance nativeBalance = Objects.requireNonNull(responseBody.getBody()).getNativeBalance();

        return Long.parseLong(nativeBalance.getLamports());
    }

    private double getPrice(RestTemplate restTemplate, String requestUrl, HttpEntity<String> entity) {
        ResponseEntity<TokenPrice> responseBody;
        try {
            responseBody = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, TokenPrice.class);
        } catch (Exception e) {
            System.out.println(e.getMessage() + requestUrl);
            throw new RuntimeException(e);
        }

        return Objects.requireNonNull(responseBody.getBody()).getUsdPrice();
    }
}
