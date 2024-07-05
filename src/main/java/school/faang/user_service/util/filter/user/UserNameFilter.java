package school.faang.user_service.util.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.util.filter.Filter;

import java.util.stream.Stream;

@Component
public class UserNameFilter implements Filter<UserFilterDto, User> {
    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return filterDto.getNamePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserFilterDto filterDto) {
        return userStream.filter(user -> user.getUsername().contains(filterDto.getNamePattern()));
    }
}
