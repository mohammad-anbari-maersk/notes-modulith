package dev.nils.notes_modulith.users.domain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.nils.notes_modulith.users.exception.UserException;
import dev.nils.notes_modulith.users.mapper.UserMapper;
import dev.nils.notes_modulith.users.web.UserCreateRequest;
import dev.nils.notes_modulith.users.UserDto;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("create() should map request, save user, and return dto")
    void create_shouldMapSaveAndReturnDto() {
        UserCreateRequest req = new UserCreateRequest("john@example.com", "John Doe");

        User mappedUser = new User();
        User savedUser = new User();
        savedUser.setId(1L);

        UserDto dto = new UserDto(1L, "john@example.com", "John Doe");

        when(userMapper.fromCreateRequest(req)).thenReturn(mappedUser);
        when(userRepository.save(mappedUser)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(dto);

        UserDto result = userService.create(req);

        assertThat(result).isEqualTo(dto);
        verify(userMapper).fromCreateRequest(req);
        verify(userRepository).save(mappedUser);
        verify(userMapper).toDto(savedUser);
    }

    @Test
    @DisplayName("findAll() should return mapped dtos")
    void findAll_shouldReturnDtos() {
        User u1 = new User();
        User u2 = new User();

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        UserDto d1 = new UserDto(1L, "a@example.com", "A");
        UserDto d2 = new UserDto(2L, "b@example.com", "B");

        when(userMapper.toDto(u1)).thenReturn(d1);
        when(userMapper.toDto(u2)).thenReturn(d2);

        List<UserDto> result = userService.findAll();

        assertThat(result).containsExactly(d1, d2);
        verify(userRepository).findAll();
        verify(userMapper).toDto(u1);
        verify(userMapper).toDto(u2);
    }

    @Test
    @DisplayName("findById() should return dto when user exists")
    void findById_shouldReturnDto() {
        User user = new User();
        user.setId(10L);

        UserDto dto = new UserDto(10L, "x@example.com", "Test User");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.findById(10L);

        assertThat(result).isEqualTo(dto);
        verify(userRepository).findById(10L);
        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("findById() should throw UserException when user not found")
    void findById_shouldThrowExceptionWhenMissing() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("User record cannot be found for id value");

        verify(userRepository).findById(999L);
        verifyNoInteractions(userMapper);
    }
}
