package ua.corporation.memeclimb.entity.main.response.helpEntity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ua.corporation.memeclimb.entity.main.response.helpEntity.TransactionInstruction;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SwapResponse {
    @JsonAlias("swapInstruction")
    private TransactionInstruction swapInstruction;
    @JsonAlias("computeBudgetInstructions")
    private List<TransactionInstruction> computeBudgetInstructions;
    @JsonAlias("setupInstructions")
    private List<TransactionInstruction> setupInstructions;
//    @JsonAlias("cleanupInstruction")
//    private List<TransactionInstruction> cleanupInstruction;
    @JsonAlias("addressLookupTableAddresses")
    private List<String> addressLookupTableAddresses;
}
