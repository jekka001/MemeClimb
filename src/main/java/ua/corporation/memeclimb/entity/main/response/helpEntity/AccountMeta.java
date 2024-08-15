package ua.corporation.memeclimb.entity.main.response.helpEntity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountMeta {
    @JsonAlias("pubkey")
    private String publicKey;
    @JsonAlias("isSigner")
    private boolean isSigner;
    @JsonAlias("isWritable")
    private boolean isWritable;

}
