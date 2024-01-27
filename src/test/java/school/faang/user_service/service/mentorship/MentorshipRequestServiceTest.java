package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @InjectMocks
    private MentorshipRequestDto mentorshipRequestDto;

    @InjectMocks
    private MentorshipRequest mentorshipRequest;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private User user;

    @BeforeEach
    public void init() {
        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequester(1L);
        mentorshipRequestDto.setDescription("Description");
        mentorshipRequestDto.setRequester(88L);
        mentorshipRequestDto.setReceiver(77L);
        mentorshipRequestDto.setCreatedAt(LocalDateTime.now());
        user = new User();
        user.setId(1L);
        user.setUsername("John");
        Mockito.when(mentorshipRequestMapper.toEntity(mentorshipRequestDto))
                .thenReturn(mentorshipRequest);
        Mockito.when(mentorshipRequestMapper.toDTO(mentorshipRequest))
                .thenReturn(mentorshipRequestDto);
    }

    @Test
    public void whenRequestForMembershipThenCreated() {
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        Mockito.verify(mentorshipRequestRepository, times(1))
                .save(mentorshipRequest);
        Mockito.verify(mentorshipRequestMapper, times(1))
                .toDTO(mentorshipRequest);
        Mockito.verify(mentorshipRequestMapper, times(1))
                .toEntity(mentorshipRequestDto);
        Mockito.verify(mentorshipRequestValidator, times(1))
                .validateUserData(any(), any());
        Assertions.assertEquals(mentorshipRequestService.requestMentorship(mentorshipRequestDto)
                , mentorshipRequestDto);
    }
}