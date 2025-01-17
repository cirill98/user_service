package school.faang.user_service.filter.userPremium;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.userPremium.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UsernameFilterPattern implements UserPremiumFilter {
    @Override
    public boolean isApplication(UserFilterDto userFilterDto) {
        return userFilterDto.getUsernameFilter() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserFilterDto userFilterDto) {
        return userStream.filter(user -> user.getUsername().equals(userFilterDto.getUsernameFilter()));
    }
}
