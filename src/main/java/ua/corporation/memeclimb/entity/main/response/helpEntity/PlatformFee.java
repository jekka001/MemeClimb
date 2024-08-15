package ua.corporation.memeclimb.entity.main.response.helpEntity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlatformFee {
    @JsonAlias("amount")
    private String amount;
    @JsonAlias("feeBps")
    private String feeBps;
}
