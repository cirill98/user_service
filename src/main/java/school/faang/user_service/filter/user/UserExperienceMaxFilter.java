package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserExperienceMaxFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getExperienceMax() > 0;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(user -> user.getExperience() < filterDto.getExperienceMax());
    }
}
