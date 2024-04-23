package school.faang.user_service.service.recommendation.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.SkillAcquiredEvent;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.dto.recommendation.RecommendationEvent;
import school.faang.user_service.handler.exception.EntityExistException;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.publisher.RecommendationEventPublisher;
import school.faang.user_service.publisher.SkillAcquiredEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.service.recommendation.filters.RecommendationRequestFilter;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;
    private final RecommendationEventPublisher recommendationEventPublisher;
    private final SkillAcquiredEventPublisher skillAcquiredEventPublisher;

    @Transactional
    @Override
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        recommendationRequestValidator.validate(recommendationRequestDto);

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        RecommendationEvent recommendationEvent = new RecommendationEvent(savedRecommendationRequest.getRecommendation().getId(),
                savedRecommendationRequest.getRecommendation().getAuthor().getId(),
                savedRecommendationRequest.getRecommendation().getReceiver().getId(),
                LocalDateTime.now());
        recommendationEventPublisher.publish(recommendationEvent);
        log.info("Отправлено уведомление по созданию рекомендации пользователя");

        List<Long> skillIds = recommendationRequestDto.getSkillIds();

        if (skillIds != null && !skillIds.isEmpty()) {
            skillIds.forEach(skillId -> {
                skillRequestRepository.create(savedRecommendationRequest.getId(), skillId);
                SkillAcquiredEvent skillAcquiredEvent = createSkillAcquiredEvent(savedRecommendationRequest, skillId);
                skillAcquiredEventPublisher.publish(skillAcquiredEvent);
                log.info("Отправлено уведомление по созданию скилла пользователю");
            });
        }

        return recommendationRequestMapper.toDto(savedRecommendationRequest);
    }

    @Override
    public List<RecommendationRequestDto> getRequests(RequestFilterDto filters) {
        List<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll();
        recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(recommendationRequests, filters));

        return recommendationRequestMapper.toDto(recommendationRequests);
    }

    @Override
    public RecommendationRequestDto getRequest(long id) {
        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository.findById(id);

        return recommendationRequestMapper.toDto(recommendationRequest.orElseThrow(() -> new EntityNotFoundException("Request not found")));
    }

    @Override
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository.findById(id);

        recommendationRequest.ifPresent(request -> {
            if (RequestStatus.REJECTED.equals(request.getStatus()) || RequestStatus.ACCEPTED.equals(request.getStatus())) {
                throw new EntityExistException("Искомый запрос рекомендации уже принят или отклонен");
            }

            request.setStatus(RequestStatus.REJECTED);
            request.setRejectionReason(rejection.getReason());
            recommendationRequestRepository.save(request);
        });

        return recommendationRequestMapper.toDto(recommendationRequest.orElse(null));
    }

    private SkillAcquiredEvent createSkillAcquiredEvent(RecommendationRequest recommendationRequest, Long skillId){
        long authorId = recommendationRequest.getRequester().getId();
        long receiverId = recommendationRequest.getReceiver().getId();
        return new SkillAcquiredEvent(skillId,authorId, receiverId);
    }
}
