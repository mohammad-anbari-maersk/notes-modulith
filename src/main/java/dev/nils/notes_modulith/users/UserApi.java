package dev.nils.notes_modulith.users;

import dev.nils.notes_modulith.users.domain.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Public entry point of the users module for other modules. */
@RequiredArgsConstructor
@Component
public class UserApi {

    private final UserService userService;

    /** Returns the user, or an empty optional when no user exists with the given id. */
    public Optional<UserDto> findById(Long userId) {
        return userService.findById(userId);
    }
}
