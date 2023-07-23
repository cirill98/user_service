package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;


@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription().isEmpty() || mentorshipRequestDto.getDescription().isBlank()){
            throw new IllegalArgumentException("Description can't be empty");
        }

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

}
