package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserExperienceMaxFilter implements UserFilter {

    @Override
    public boolean isAcceptable(UserFilterDto userFilterDto) {
        return userFilterDto.getExperienceMax() != null;
    }

    @Override
    public Stream<User> applyFilter(Stream<User> users, UserFilterDto userFilterDto) {
        return users.filter(user -> user.getExperience() <= userFilterDto.getExperienceMax());
    }
}