package ua.corporation.memeclimb.entity.main.dto;

import lombok.Getter;
import lombok.Setter;
import ua.corporation.memeclimb.entity.main.Participant;
import ua.corporation.memeclimb.entity.main.Winner;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserDto {
    private UUID id;
    private String name;
    private String telegramId;
    private byte[] privateKey;
    private String publicKey;
    private int page;
    private UUID chosenPoolId;
    private List<Participant> participants;
    private List<Winner> winPool;
    private boolean initializedWithdraw;
    private boolean deactivate;

    public UserDto(String name, String telegramId) {
        this.name = name;
        this.telegramId = telegramId;
    }

}
