package ua.corporation.memeclimb.mapper;

import org.mapstruct.Mapper;
import ua.corporation.memeclimb.entity.main.Coin;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CoinMapper {
    Coin toEntity(CoinDto coinDto);

    CoinDto toDto(Coin coin);

    List<CoinDto> toListDto(List<Coin> coins);
}
