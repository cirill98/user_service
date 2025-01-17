package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.recommendation.RecommendationEventMapper;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.messaging.publisher.recommendation.RecommendationEventPublisher;
import school.faang.user_service.messaging.publisher.recommendationReceived.RecommendationReceivedEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.skillOffer.SkillOfferService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.recommendation.RecommendationValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final UserService userService;
    private final RecommendationValidator recommendationValidator;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationEventMapper recommendationEventMapper;
    private final SkillOfferService skillOfferService;
    private final RecommendationEventPublisher recommendationEventPublisher;
    private final RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        recommendationValidator.checkNotRecommendBeforeSixMonths(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        recommendationValidator.validateSkillOffers(recommendationDto);

        Recommendation recommendationReadyToSave = recommendationMapper.toEntity(recommendationDto);
        Recommendation savedRecommendation = recommendationRepository.save(recommendationReadyToSave);
        skillOfferService.updateSkillGuarantee(recommendationDto.getSkillOffers(),
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId());

        List<SkillOffer> savedSkillOffers = skillOfferService.saveSkillOffers(recommendationDto.getSkillOffers(), savedRecommendation.getId());
        savedRecommendation.setSkillOffers(savedSkillOffers);

        RecommendationDto savedRecommendationDto = recommendationMapper.toDto(savedRecommendation);
        recommendationEventPublisher.publish(recommendationEventMapper.toEvent(savedRecommendationDto));
        recommendationReceivedEventPublisher.publish(recommendationMapper.toRecommendationReceivedEvent(savedRecommendationDto));

        return savedRecommendationDto;
    }

    @Transactional
    public RecommendationDto update(long recommendationId, RecommendationDto updateRecommendationDto) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> {
                    log.error("ошибка");
                    return new EntityNotFoundException("ошибка");
                });

        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        RecommendationDto recommendationDto = recommendationMapper.toDto(recommendation);

        recommendationValidator.checkNotRecommendBeforeSixMonths(recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        recommendationValidator.validateSkillOffers(updateRecommendationDto);

        List<SkillOfferDto> skillOfferDtosToUpdate =
                skillOfferService.newSkillOffersToUpdate(recommendationDto.getSkillOffers(), updateRecommendationDto.getSkillOffers());
        skillOfferService.updateSkillGuarantee(skillOfferDtosToUpdate, recommendationDto.getAuthorId(), recommendationDto.getReceiverId());
        List<SkillOffer> savedSkillOffersToUpdate = skillOfferService.saveSkillOffers(skillOfferDtosToUpdate, recommendationId);

        recommendationDto.setContent(updateRecommendationDto.getContent());
        Recommendation updatedRecommendation = recommendationRepository.save(recommendationMapper.toEntity(recommendationDto));

        skillOffers.addAll(savedSkillOffersToUpdate);
        updatedRecommendation.setSkillOffers(skillOffers);

        RecommendationDto updatedRecommendationDto = recommendationMapper.toDto(updatedRecommendation);
        recommendationEventPublisher.publish(recommendationEventMapper.toEvent(updatedRecommendationDto));

        return updatedRecommendationDto;
    }

    public void delete(long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        return recommendationMapper.toListDto(userService.getUserById(recieverId).getRecommendationsReceived());
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationMapper.toListDto(userService.getUserById(authorId).getRecommendationsGiven());
    }
}
