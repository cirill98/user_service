package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import school.faang.user_service.controller.ApiPath;
import school.faang.user_service.controller.BaseControllerTest;
import school.faang.user_service.dto.DtoValidationConstraints;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MentorshipRequestController.class)
class MentorshipRequestControllerTest extends BaseControllerTest {

    @MockBean
    private MentorshipRequestService mentorshipRequestService;

    @Test
    void requestMentorship_should_return_created_status_with_valid_dto() throws Exception {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setDescription("Test Description");
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);

        MentorshipRequestDto savedDto = new MentorshipRequestDto();
        savedDto.setId(1L);
        savedDto.setDescription("Test Description");
        savedDto.setRequesterId(1L);
        savedDto.setReceiverId(2L);
        savedDto.setRequestStatus(RequestStatus.PENDING);

        when(mentorshipRequestService.requestMentorship(any(MentorshipRequestDto.class))).thenReturn(savedDto);

        mockMvc.perform(post(ApiPath.REQUEST_MENTORSHIP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(BaseControllerTest.USER_HEADER, BaseControllerTest.DEFAULT_HEADER_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedDto.getId()))
                .andExpect(jsonPath("$.description").value(savedDto.getDescription()))
                .andExpect(jsonPath("$.requestStatus").value("PENDING"));
    }

    @Test
    void requestMentorship_should_return_bad_request_with_missing_desc() throws Exception {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);

        mockMvc.perform(post(ApiPath.REQUEST_MENTORSHIP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(BaseControllerTest.USER_HEADER, BaseControllerTest.DEFAULT_HEADER_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DtoValidationConstraints.VALIDATION_FAILED))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void requestMentorship_should_return_bad_request_if_no_people_are_referenced() throws Exception {
        MentorshipRequestDto dto = new MentorshipRequestDto();

        mockMvc.perform(post(ApiPath.REQUEST_MENTORSHIP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(BaseControllerTest.USER_HEADER, BaseControllerTest.DEFAULT_HEADER_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DtoValidationConstraints.VALIDATION_FAILED))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void getRequests_should_return_filtered_requests() throws Exception {
        var requestFilterDto = new RequestFilterDto();
        requestFilterDto.setRequestStatusPattern("PENDING");

        MentorshipRequestDto dto1 = new MentorshipRequestDto();
        dto1.setId(1L);
        dto1.setRequestStatus(RequestStatus.PENDING);
        MentorshipRequestDto dto2 = new MentorshipRequestDto();
        dto2.setId(2L);
        dto2.setRequestStatus(RequestStatus.PENDING);
        MentorshipRequestDto dto3 = new MentorshipRequestDto();
        dto3.setId(3L);
        dto3.setRequestStatus(RequestStatus.PENDING);
        var requests = List.of(dto1, dto2, dto3);

        when(mentorshipRequestService.getRequests(requestFilterDto)).thenReturn(requests);

        mockMvc.perform(get(ApiPath.REQUEST_MENTORSHIP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestFilterDto))
                        .header(BaseControllerTest.USER_HEADER, BaseControllerTest.DEFAULT_HEADER_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[0].id").value(dto1.getId()))
                .andExpect(jsonPath("$[1].id").value(dto2.getId()))
                .andExpect(jsonPath("$[2].id").value(dto3.getId()));
    }

    @Test
    void getRequests_should_return_bad_request_when_filter_dto_is_not_present() throws Exception {
        mockMvc.perform(get(ApiPath.REQUEST_MENTORSHIP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null))
                        .header(BaseControllerTest.USER_HEADER, BaseControllerTest.DEFAULT_HEADER_VALUE))
                .andExpect(status().isBadRequest());
    }
}