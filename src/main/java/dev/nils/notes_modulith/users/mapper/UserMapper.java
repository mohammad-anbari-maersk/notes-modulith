package dev.nils.notes_modulith.users.mapper;

import dev.nils.notes_modulith.users.domain.User;
import dev.nils.notes_modulith.users.web.UserCreateRequest;
import dev.nils.notes_modulith.users.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User fromCreateRequest(UserCreateRequest req);
}
