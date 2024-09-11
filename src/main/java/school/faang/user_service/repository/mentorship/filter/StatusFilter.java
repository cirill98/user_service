package school.faang.user_service.repository.mentorship.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto_mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.stream.Stream;

@Component
@Data
@AllArgsConstructor
public class StatusFilter implements MentorshipRequestFilter {


    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters) {
        return requests.filter(request -> request.getStatus().equals(filters.getStatus()));
    }
}
