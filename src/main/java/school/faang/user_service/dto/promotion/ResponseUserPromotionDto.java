package school.faang.user_service.dto.promotion;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResponseUserPromotionDto(
        long id,
        long userId,
        int numberOfViews,
        int audienceReach,
        LocalDateTime creationDate
) {
}
