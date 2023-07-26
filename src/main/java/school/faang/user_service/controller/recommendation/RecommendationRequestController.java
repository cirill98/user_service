package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isBlank()) {
            throw new DataValidationException("Recommendation request message should not be empty");
        } else {
            return recommendationRequestService.create(recommendationRequest);
        }
    }
    
    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRequest(id);
    }
  
  public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        if (rejection.getReason() == null || rejection.getReason().isBlank()) {
            throw new DataValidationException("Recommendation request rejection reason should not be empty");
        } else {
            return recommendationRequestService.rejectRequest(id, rejection);
        }
    }
}
