package dev.nils.notes_modulith.users.domain;

import dev.nils.notes_modulith.users.UserDto;
import dev.nils.notes_modulith.users.mapper.UserMapper;
import dev.nils.notes_modulith.users.web.UserCreateRequest;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional
    public UserDto create(UserCreateRequest req) {
        User user = userMapper.fromCreateRequest(req);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }
}
