package dev.nils.notes_modulith.users.domain;

import dev.nils.notes_modulith.users.exception.UserException;
import dev.nils.notes_modulith.users.exception.model.UserErrorCode;
import dev.nils.notes_modulith.users.mapper.UserMapper;
import dev.nils.notes_modulith.users.web.UserCreateRequest;
import dev.nils.notes_modulith.users.UserDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserDto create(UserCreateRequest req) {
        User user = userMapper.fromCreateRequest(req);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        return userRepository.findById(id).map(userMapper::toDto).orElseThrow(() -> {
            log.error("[USERS_SERVICE] [FAILED] [NOTE_NOT_FOUND_WITH_ID] [ID={}]", id);
            return new UserException(UserErrorCode.USER_NOT_FOUND_WITH_ID, id);
        });
    }
}
