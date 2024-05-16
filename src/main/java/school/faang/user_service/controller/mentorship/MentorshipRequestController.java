package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.context.UserHeaderFilter;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/mentorship/request")
@RequiredArgsConstructor
@Validated
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final UserContext userContext;

    @PostMapping(path = "/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipRequestDto requestMentorship(
            @Positive @RequestParam(name = "mentorId") Long receiverId,
            @Valid @RequestBody MentorshipRequestDto dto) {
        var requesterId = userContext.getUserId();
        return mentorshipRequestService.requestMentorship(requesterId, receiverId, dto);
    }

    @GetMapping
    public List<MentorshipRequestDto> getRequests(@Valid @RequestBody RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PutMapping(path = "/accept/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MentorshipRequestDto acceptRequest(@Positive @PathVariable(name = "id") Long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping(path = "/reject/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MentorshipRequestDto rejectRequest(@Positive @PathVariable(name = "id") Long id,
                                              @RequestBody RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}