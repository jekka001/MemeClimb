package ua.corporation.memeclimb.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.response.helpEntity.QuoteResponse;
import ua.corporation.memeclimb.entity.main.response.helpEntity.SwapResponse;
import ua.corporation.memeclimb.service.JupiterService;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JupiterServiceImpl implements JupiterService {
    private static final Integer AMOUNT = 100000;
    private static final String USDT_ACCOUNT = "Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB";
    private static final String SWAP_INSTRUCTIONS = "/swap-instructions";
    private static final String QUOTE = "/quote";
    private static final String BODY = "body";
    private static final String ERROR_MESSAGE = "some problem with make parameters";
    private static final String INPUT_MINT_NAME = "inputMint";
    private static final String OUTPUT_MINT_NAME = "outputMint";
    private static final String AMOUNT_NAME = "amount";
    private static final String AS_LEGACY_TRANSACTION_NAME = "asLegacyTransaction";
    private static final Boolean AS_LEGACY_TRANSACTION = true;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${jupiter.url}")
    private String jupiterUrl;


    @Override
    public SwapResponse getSwapTransaction(CoinDto coinDto, String serverPublicKey, double usdPrize, String associatedPublicKey) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = jupiterUrl + SWAP_INSTRUCTIONS;
        HttpEntity<String> entity = prepareEntity(coinDto, usdPrize, serverPublicKey, associatedPublicKey);

        return prepareResponse(restTemplate, requestUrl, entity);
    }

    private HttpEntity<String> prepareEntity(CoinDto coinDto, double usdPrize, String serverPublicKey, String associatedPublicKey) {
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject parameters = getRequestParameters(coinDto, usdPrize, serverPublicKey, associatedPublicKey);

        return new HttpEntity<>(parameters.toString(), headers);
    }

    private JSONObject getRequestParameters(CoinDto coinDto, double usdPrize, String serverPublicKey, String associatedPublicKey) {
        QuoteResponse quoteResponse = prepareQuoteResponse(coinDto.getMint(), Double.valueOf(usdPrize).intValue());
        Map<String, Object> map = getMapWithParameters(quoteResponse);
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("userPublicKey", serverPublicKey);
        jsonObject.put("wrapAndUnwrapSol", true);
        jsonObject.put("prioritizationFeeLamports", Collections.singletonMap("autoMultiplier", 6));
        jsonObject.put("destinationTokenAccount", associatedPublicKey);
        jsonObject.put("dynamicComputeUnitLimit", true);
        jsonObject.put("skipUserAccountsRpcCalls", true);
        jsonObject.put("quoteResponse", map);

        return jsonObject;
    }

    private QuoteResponse prepareQuoteResponse(String tokenAddressTo, Integer amount) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = prepareURL(tokenAddressTo, amount);
        HttpEntity<String> entity = prepareEntity();

        ResponseEntity<QuoteResponse> responseBody =
                restTemplate.exchange(requestUrl, HttpMethod.GET, entity, QuoteResponse.class);

        return responseBody.getBody();
    }

    private String prepareURL(String outputMint, Integer amount) {
        return UriComponentsBuilder.fromHttpUrl(jupiterUrl + QUOTE)
                .queryParam(INPUT_MINT_NAME, USDT_ACCOUNT)
                .queryParam(OUTPUT_MINT_NAME, outputMint)
                .queryParam(AMOUNT_NAME, amount)
                .queryParam(AS_LEGACY_TRANSACTION_NAME, AS_LEGACY_TRANSACTION)
                .encode()
                .toUriString();
    }

    private Map<String, Object> getMapWithParameters(QuoteResponse quoteResponse) {
        try {
            String jsonQuote = objectMapper.writeValueAsString(quoteResponse);
            return objectMapper.readValue(jsonQuote, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(ERROR_MESSAGE);
        }
    }

    private HttpEntity<String> prepareEntity() {
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return new HttpEntity<>(BODY, headers);
    }

    private SwapResponse prepareResponse(RestTemplate restTemplate, String requestUrl, HttpEntity<String> entity) {
        ResponseEntity<SwapResponse> responseBody =
                restTemplate.exchange(requestUrl, HttpMethod.POST, entity, SwapResponse.class);

        return responseBody.getBody();
    }
}
