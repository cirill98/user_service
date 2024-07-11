package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.userDto.UserDto;
import school.faang.user_service.dto.userDto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.userPremium.UserPremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.filter.userFilter.UserFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserPremiumMapper userPremiumMapper;
    private final List<UserFilter> userFilters;

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> userStream = userRepository.findPremiumUsers();
        List<User> resultPremiumUserList = getFilterUser(userStream, userFilterDto);
        return resultPremiumUserList.stream().map(userPremiumMapper::toDto).toList();
    }

    private List<User> getFilterUser(Stream<User> userStream, UserFilterDto userFilterDto) {
        List<UserFilter> userFiltersTrue = userFilters.stream()
                .filter(filter -> filter.isApplication(userFilterDto))
                .toList();
        List<User> filterUser = new ArrayList<>();
        for (UserFilter userFilter : userFiltersTrue) {
            filterUser = userFilter
                    .apply(userStream, userFilterDto).toList();
        }
        return filterUser;
    }
}
