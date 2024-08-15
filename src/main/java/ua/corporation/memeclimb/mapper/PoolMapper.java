package ua.corporation.memeclimb.mapper;

import org.mapstruct.Mapper;
import ua.corporation.memeclimb.entity.main.Pool;
import ua.corporation.memeclimb.entity.main.dto.PoolDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PoolMapper {
    Pool toEntity(PoolDto poolDto);

    PoolDto toDto(Pool pool);

    List<PoolDto> toListDto(List<Pool> pools);
}
