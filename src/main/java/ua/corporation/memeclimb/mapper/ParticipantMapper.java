package ua.corporation.memeclimb.mapper;

import org.mapstruct.Mapper;
import ua.corporation.memeclimb.entity.main.Participant;
import ua.corporation.memeclimb.entity.main.dto.ParticipantDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {
    Participant toEntity(ParticipantDto participantDto);

    ParticipantDto toDto(Participant participant);

    List<ParticipantDto> toListDto(List<Participant> participants);
}
