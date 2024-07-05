package school.faang.user_service.util.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.util.filter.Filter;

import java.util.stream.Stream;

@Component
public class UserExpMinFilter implements Filter<UserFilterDto, User> {
    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return filterDto.getExperienceMin() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserFilterDto filterDto) {
        return userStream.filter(user -> user.getExperience() >= filterDto.getExperienceMin());
    }
}
