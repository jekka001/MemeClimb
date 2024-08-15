package ua.corporation.memeclimb.entity.main.response.helpEntity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SwapInfo {
    @JsonAlias("ammKey")
    private String ammKey;
    @JsonAlias("label")
    private String label;
    @JsonAlias("inputMint")
    private String inputMint;
    @JsonAlias("outputMint")
    private String outputMint;
    @JsonAlias("inAmount")
    private String inAmount;
    @JsonAlias("outAmount")
    private String outAmount;
    @JsonAlias("feeAmount")
    private String feeAmount;
    @JsonAlias("feeMint")
    private String feeMint;
}
