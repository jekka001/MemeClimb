package ua.corporation.memeclimb.entity.main.response.helpEntity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuoteResponse {
    @JsonAlias("inputMint")
    private String inputMint;
    @JsonAlias("inAmount")
    private String inAmount;
    @JsonAlias("outputMint")
    private String outputMint;
    @JsonAlias("outAmount")
    private String outAmount;
    @JsonAlias("otherAmountThreshold")
    private String otherAmountThreshold;
    @JsonAlias("swapMode")
    private String swapMode;
    @JsonAlias("slippageBps")
    private int slippageBps;
    @JsonAlias("platformFee")
    private PlatformFee platformFee;
    @JsonAlias("priceImpactPct")
    private String priceImpactPct;
    @JsonAlias("routePlan")
    List<RoutePlan> routePlan;
    @JsonAlias("contextSlot")
    private long contextSlot;
    @JsonAlias("timeTaken")
    private double timeTaken;
}

