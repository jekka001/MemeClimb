package ua.corporation.memeclimb.service;

import ua.corporation.memeclimb.entity.main.Participant;
import ua.corporation.memeclimb.entity.main.dto.ParticipantDto;
import ua.corporation.memeclimb.entity.main.dto.PoolDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;

public interface ParticipantService {

    ParticipantDto get(PoolDto pool, UserDto user);

    int getUserStep(PoolDto pool, UserDto user);

    ParticipantDto save(PoolDto pool, UserDto user);

    void save(ParticipantDto participantDto);

    void changeStep(PoolDto pool, UserDto user);

    void delete();
}
