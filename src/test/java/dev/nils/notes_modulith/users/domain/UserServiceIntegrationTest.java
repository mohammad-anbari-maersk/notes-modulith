package dev.nils.notes_modulith.users.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.nils.notes_modulith.TestContainersConfiguration;
import dev.nils.notes_modulith.users.web.UserCreateRequest;
import dev.nils.notes_modulith.users.UserDto;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestContainersConfiguration.class)
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("findAll() should return users from data.sql")
    void findAll_shouldReturnAllSeedUsers() {
        List<UserDto> users = userService.findAll();

        assertThat(users)
                .isNotEmpty()
                .extracting(UserDto::email)
                .contains("jane.doe@example.com", "john.doe@example.com");
    }

    @Test
    @DisplayName("findById() should return user when exists")
    void findById_shouldReturnUser() {
        Long janeId =
                userRepository.findByEmail("jane.doe@example.com").orElseThrow().getId();

        UserDto dto = userService.findById(janeId).orElseThrow();

        assertThat(dto.email()).isEqualTo("jane.doe@example.com");
        assertThat(dto.fullName()).isEqualTo("Jane Doe");
    }

    @Test
    @DisplayName("findById() should return empty when ID does not exist")
    void findById_shouldReturnEmptyWhenNotExists() {
        assertThat(userService.findById(9999L)).isEmpty();
    }

    @Test
    @DisplayName("create() should persist and return saved user")
    void create_shouldPersistUser() {
        UserCreateRequest req = new UserCreateRequest("test.user@example.com", "Test User");

        UserDto saved = userService.create(req);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.email()).isEqualTo("test.user@example.com");
        assertThat(saved.fullName()).isEqualTo("Test User");

        Optional<User> userOpt = userRepository.findByEmail("test.user@example.com");
        assertThat(userOpt).isPresent();
    }
}
