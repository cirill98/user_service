package school.faang.user_service.dto.mentorship;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.dto.DtoValidationConstraints;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
public class MentorshipRequestDto {

    private Long id;
    @NotBlank(message = DtoValidationConstraints.MENTORSHIP_REQUEST_DESCRIPTION_CONSTRAINT)
    private String description;
    @NotNull(message = DtoValidationConstraints.MENTORSHIP_REQUEST_REQUESTER_ID_CONSTRAINT)
    private Long requesterId;
    @NotNull(message = DtoValidationConstraints.MENTORSHIP_REQUEST_RECEIVER_ID_CONSTRAINT)
    private Long receiverId;
    private RequestStatus requestStatus;
    private String rejectionReason;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime updatedAt;
}
