package ua.corporation.memeclimb.entity.main.response.helpEntity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountBalance {
    @JsonAlias("tokens")
    private List<CoinDto> coins;
    @JsonAlias("nativeBalance")
    private NativeBalance nativeBalance;
}