package ua.corporation.memeclimb.mapper;

import org.mapstruct.Mapper;
import ua.corporation.memeclimb.entity.main.User;
import ua.corporation.memeclimb.entity.main.dto.UserDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto userDto);

    UserDto toDto(User user);

    List<UserDto> toListDto(List<User> users);
}
