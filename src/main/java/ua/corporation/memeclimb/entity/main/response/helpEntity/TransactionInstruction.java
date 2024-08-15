package ua.corporation.memeclimb.entity.main.response.helpEntity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionInstruction {
    @JsonAlias("programId")
    private String pubkey;
    @JsonAlias("accounts")
    private List<AccountMeta> accounts;
    @JsonAlias("data")
    private String data;
}
