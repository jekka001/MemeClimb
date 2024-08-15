package ua.corporation.memeclimb.entity.main.dto;

import lombok.Getter;
import lombok.Setter;
import ua.corporation.memeclimb.entity.main.Pool;
import ua.corporation.memeclimb.entity.main.User;

import java.util.UUID;

@Getter
@Setter
public class WinnerDto {
    private UUID id;
    private Pool pool;
    private User user;
}
