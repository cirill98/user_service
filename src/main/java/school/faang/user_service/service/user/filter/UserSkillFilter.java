package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.Objects;
import java.util.stream.Stream;

public class UserSkillFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return Objects.nonNull(filters.getSkillPattern());
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user ->
                user.getSkills().stream()
                        .anyMatch(skill -> skill.getTitle().contains(filters.getSkillPattern())));
    }
}