package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import school.faang.user_service.controller.MentorshipRequestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.RequestsResponse;
import school.faang.user_service.util.exception.GetRequestsMentorshipsException;
import school.faang.user_service.util.exception.RequestMentorshipException;


public class MentorshipRequestControllerTest {

    @Mock
    MentorshipRequestService mentorshipRequestService;

    @InjectMocks
    MentorshipRequestController mentorshipRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRequestMentorship_RequestIsValid_ShouldReturnValidResponseEntity() {
        MentorshipRequestDto mentorshipRequestDto =
                new MentorshipRequestDto("description", 1L, 2L);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<HttpStatus> httpStatusResponseEntity =
                this.mentorshipRequestController.requestMentorship(mentorshipRequestDto, bindingResult);

        Assertions.assertNotNull(httpStatusResponseEntity);
        Assertions.assertEquals(HttpStatus.OK, httpStatusResponseEntity.getStatusCode());
    }

    @Test
    void testRequestMentorship_RequestIsInvalid_ShouldReturnException() {
        MentorshipRequestDto mentorshipRequestDto =
                new MentorshipRequestDto("description", null, 1L);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        Assertions.assertThrows(RequestMentorshipException.class,
                () -> this.mentorshipRequestController.requestMentorship(mentorshipRequestDto, bindingResult));
    }

    @Test
    void testGetRequests_RequestIsValid_ShouldReturnValidResponseEntity() {
        RequestFilterDto requestFilterDto =
                new RequestFilterDto("description", null, null, "PENDING");
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        RequestsResponse requestsResponse =
                mentorshipRequestController.getRequests(requestFilterDto, bindingResult);

        Assertions.assertNotNull(requestsResponse);
    }

    @Test
    void testGetRequests_RequestIsInvalid_ShouldReturnException() {
        RequestFilterDto requestFilterDto =
                new RequestFilterDto("description", null, null, "pending");
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        Assertions.assertThrows(GetRequestsMentorshipsException.class,
                () -> this.mentorshipRequestController.getRequests(requestFilterDto, bindingResult));
    }
}
