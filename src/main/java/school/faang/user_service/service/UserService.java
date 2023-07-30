package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentor.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Setter
public class UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();

        premiumUsers = filter(userFilterDto, premiumUsers);
        return userMapper.toUserListDto(premiumUsers.toList());
    }

    private Stream<User> filter(UserFilterDto userFilterDto, Stream<User> premiumUsers) {
        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(userFilterDto)) {
                premiumUsers = filter.apply(premiumUsers, userFilterDto);
            }
        }
        return premiumUsers;
    }
}
