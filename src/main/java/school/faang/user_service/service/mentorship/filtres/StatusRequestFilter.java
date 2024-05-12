package school.faang.user_service.service.mentorship.filtres;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class StatusRequestFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests, RequestFilterDto filter) {
        return mentorshipRequests.filter(mentorshipRequest -> !mentorshipRequest.getStatus().equals(filter.getStatus()));
    }
}