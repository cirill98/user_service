package school.faang.user_service.filter.user_filter;

import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class PageFilter implements UserFilter {
    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filters) {
        return filters.getPage() != 0 && filters.getPageSize() != 0;
    }

    @Override
    public void apply(Stream<User> users, SubscriptionUserFilterDto subscriptionUserFilterDto) {
        users.skip(subscriptionUserFilterDto.getPage() * subscriptionUserFilterDto.getPageSize());
    }
}
