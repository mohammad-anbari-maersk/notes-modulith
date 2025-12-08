package dev.nils.notes_modulith.users;

import dev.nils.notes_modulith.users.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserApi {

    private final UserService userService;

    public UserDto getById(Long userId) {
        return userService.findById(userId);
    }
}
