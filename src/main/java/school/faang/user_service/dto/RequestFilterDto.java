package school.faang.user_service.dto;

import lombok.Data;

@Data
public class RequestFilterDto {
    private Long requesterId;
    private Long receiverId;
}
