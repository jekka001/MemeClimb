package ua.corporation.memeclimb.mapper;

import org.mapstruct.Mapper;
import ua.corporation.memeclimb.entity.main.Step;
import ua.corporation.memeclimb.entity.main.dto.StepDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StepMapper {
    Step toEntity(StepDto stepDto);

    StepDto toDto(Step step);

    List<StepDto> toListDto(List<Step> steps);
}
