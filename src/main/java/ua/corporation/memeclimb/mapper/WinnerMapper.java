package ua.corporation.memeclimb.mapper;

import org.mapstruct.Mapper;
import ua.corporation.memeclimb.entity.main.Winner;
import ua.corporation.memeclimb.entity.main.dto.WinnerDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WinnerMapper {
    Winner toEntity(WinnerDto winnerDto);

    WinnerDto toDto(Winner winner);

    List<WinnerDto> toListDto(List<Winner> winners);
}
