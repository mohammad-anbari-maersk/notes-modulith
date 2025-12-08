package dev.nils.notes_modulith.users.web;

import dev.nils.notes_modulith.users.UserDto;
import dev.nils.notes_modulith.users.domain.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService service;

    @PostMapping
    public UserDto create(@RequestBody UserCreateRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<UserDto> all() {
        return service.findAll();
    }
}
