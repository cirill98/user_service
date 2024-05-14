package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship/request")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipResponseDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.validateRequest(mentorshipRequestDto);
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @GetMapping("/filter")
    public List<MentorshipRequestDto> getRequests(@RequestBody MentorshipRequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PostMapping("/approval/{id}")
    public MentorshipResponseDto acceptRequest(@PathVariable long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PostMapping("/rejection/{id}")
    public MentorshipResponseDto rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        mentorshipRequestValidator.validateRejectionDto(rejection);
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}