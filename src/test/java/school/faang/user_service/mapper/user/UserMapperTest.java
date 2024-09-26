package school.faang.user_service.mapper.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private final static long USER_ID_ONE = 1L;
    private final static long USER_ID_TWO = 2L;
    private static final String USER_NAME_ONE = "name";
    private static final String USER_NAME_TWO = "Name";
    private static final int USER_DTOS_SIZE = 2;
    private final static int SIZE_USER_DTO_LIST = 2;
    @InjectMocks
    private UserMapperImpl userMapper;
    private User userOne;
    private User userTwo;
    private List<User> users;

    @BeforeEach
    public void init() {
        userOne = User.builder()
                .id(USER_ID_ONE)
                .username(USER_NAME_ONE)
                .build();

        userTwo = User.builder()
                .id(USER_ID_TWO)
                .username(USER_NAME_TWO)
                .build();
        users = List.of(userOne, userTwo);
    }

    @Nested
    class ToDto {

        @Test
        @DisplayName("If gets null than return null")
        void whenListUsersIsNullThenGetNull() {
            assertNull(userMapper.toDtos(null));
        }

        @Test
        @DisplayName("Успех маппинга User в UserDto")
        public void whenUserIsNotNullThenReturnUserToDto() {
            UserDto userDto = userMapper.toDto(userOne);

            assertNotNull(userDto);
            assertEquals(userOne.getId(), userDto.getId());
            assertEquals(userOne.getUsername(), userDto.getUsername());
        }

        @Test
        @DisplayName("When gets List<User> with size 2 than return List<UserDto> with size 2")
        void whenListOfUsersIsNotNullThenGetListOfUserDtos() {
            List<User> users = List.of(
                    User.builder()
                            .id(USER_ID_ONE)
                            .username("User")
                            .email("Email")
                            .build(),
                    User.builder()
                            .id(USER_ID_TWO)
                            .username("User1")
                            .email("Email1")
                            .active(true)
                            .build());

            List<UserDto> userDtos = userMapper.toDtos(users);

            assertEquals(SIZE_USER_DTO_LIST, userDtos.size());
        }

        @Test
        @DisplayName("Успех маппинга List<User> в List<UserDto>")
        public void whenListOfUserIsNotNullThenReturnListUserDtos() {
            List<UserDto> userDtos = userMapper.toDtos(users);

            assertNotNull(userDtos);
            assertEquals(USER_DTOS_SIZE, userDtos.size());
            assertEquals(userOne.getId(), userDtos.get(0).getId());
            assertEquals(userOne.getUsername(), userDtos.get(0).getUsername());
            assertEquals(userTwo.getId(), userDtos.get(1).getId());
            assertEquals(userTwo.getUsername(), userDtos.get(1).getUsername());
        }
    }
}