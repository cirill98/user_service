package school.faang.user_service.service.recommendationRequest;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendationRequest.RecommendationRejectionDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.filter.recommendationRequest.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendationRequest.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static final int COUNT_MONTHS = 6;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final List<RecommendationRequestFilter> recommendationRequestFilter;
    private final RecommendationRequestedPublishService recommendationRequestedPublishService;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        checkExistenceObjectByIds(recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId());
        heckForRequestsOfSixMonths(recommendationRequestDto);
        List<SkillRequest> skills = checkSkillRequestInDatabase(recommendationRequestDto);

        RecommendationRequest newRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);
        recommendationRequestRepository.save(newRequest);

        skills.forEach(skill -> skillRequestRepository.create(newRequest.getId(), skill.getId()));

        recommendationRequestedPublishService.eventPublish(newRequest);
        return recommendationRequestMapper.toDto(newRequest);
    }

    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto filter) {
        Stream<RecommendationRequest> recommendationRequestsAll = recommendationRequestRepository.findAll().stream();
        List<RecommendationRequest> result = recommendationRequestFilter.stream()
                .filter(filterOne -> filterOne.isApplication(filter))
                .reduce(recommendationRequestsAll, (cumulativeStream, filterOne) ->
                        filterOne.apply(cumulativeStream, filter), Stream::concat)
                .toList();
        return result.stream()
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest request = recommendationRequestRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error(ExceptionMessages.RECOMMENDATION_REQUEST_NOT_FOUND);
                    return new NoSuchElementException(ExceptionMessages.RECOMMENDATION_REQUEST_NOT_FOUND);
                });
        return recommendationRequestMapper.toDto(request);
    }

    public RecommendationRequestDto rejectRequest(long id, RecommendationRejectionDto recommendationRejectionDto) {
        return recommendationRequestRepository.findById(id)
                .map(request -> {
                    if (request.getStatus() == RequestStatus.PENDING) {
                        request.setStatus(RequestStatus.REJECTED);
                        request.setRejectionReason(recommendationRejectionDto.getReason());
                        recommendationRequestRepository.save(request);
                        return recommendationRequestMapper.toDto(request);
                    } else {
                        log.error(ExceptionMessages.DISCREPANCY_OF_STATUS);
                        throw new IllegalStateException(ExceptionMessages.DISCREPANCY_OF_STATUS);
                    }
                }).orElseThrow(() -> {
                    log.error(ExceptionMessages.RECOMMENDATION_REQUEST_NOT_FOUND);
                    return new NoSuchElementException(ExceptionMessages.RECOMMENDATION_REQUEST_NOT_FOUND);
                });
    }

    private void checkExistenceObjectByIds(long requesterId, long receiverId) {
        if (!userRepository.existsById(requesterId) || !userRepository.existsById(receiverId)) {
            log.error(ExceptionMessages.RECOMMENDATION_REQUEST_NOT_FOUND);
            throw new EntityNotFoundException(ExceptionMessages.RECOMMENDATION_REQUEST_NOT_FOUND);
        }
    }

    private void heckForRequestsOfSixMonths(RecommendationRequestDto recommendationRequestDto) {
        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository
                .findLatestPendingRequest(recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId());
        if (recommendationRequest.isPresent()) {
            LocalDateTime localDateTime = LocalDateTime.now().minus(COUNT_MONTHS, ChronoUnit.MONTHS);
            if (recommendationRequest.get().getUpdatedAt().isAfter(localDateTime)) {
                log.error(ExceptionMessages.RECOMMENDATION_FREQUENCY);
                throw new IllegalStateException(ExceptionMessages.RECOMMENDATION_FREQUENCY);
            }
        }
    }

    private List<SkillRequest> checkSkillRequestInDatabase(RecommendationRequestDto recommendationRequestDto) {
        List<Long> skillsId = recommendationRequestDto.getSkillsId();
        List<SkillRequest> skills = StreamSupport
                .stream(skillRequestRepository.findAllById(skillsId).spliterator(), false).toList();
        if (skills.isEmpty()) {
            log.error(ExceptionMessages.REQUEST_SKILL);
            throw new NullPointerException(ExceptionMessages.REQUEST_SKILL);
        }
        return skills;
    }
}