package school.faang.user_service.service.skill;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.message.ExceptionMessage;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static school.faang.user_service.exception.message.ExceptionMessage.NO_SUCH_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class SkillService {
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;

    @Transactional
    public SkillDto create(SkillDto skillDto) {
        validateSkill(skillDto);
        Skill skillEntity = skillMapper.dtoToSkill(skillDto);
        return skillMapper.skillToDto(skillRepository.save(skillEntity));
    }

    @Transactional
    public List<SkillDto> getUserSkills(long userId) {
        User user  = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException(NO_SUCH_USER_EXCEPTION.getMessage()));

        List<Skill> ownerSkillsList = skillRepository.findAllByUserId(userId);
        return skillMapper.skillToDto(ownerSkillsList);
    }

    @Transactional
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);

        Map<Skill, Long> skillCountMap = skillsOfferedToUser.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return skillCountMap.entrySet().stream()
                .map(entry -> SkillCandidateDto.builder()
                        .skillDto(skillMapper.skillToDto(entry.getKey()))
                        .offersAmount(entry.getValue())
                        .build())
                .toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        Skill userSkill = skillRepository.findUserSkill(skillId, userId)
                .orElseThrow(() -> new ValidationException(ExceptionMessage.USER_SKILL_NOT_FOUND.getMessage()));

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        validateMinSkillOffers(skillOffers.size(), skillId, userId);

        skillRepository.assignSkillToUser(skillId, userId);
        addUserSkillGuarantee(userSkill, skillOffers);

        return skillMapper.skillToDto(userSkill);
    }


    private void addUserSkillGuarantee(Skill userSkill, List<SkillOffer> allOffersOfSkill) {
        allOffersOfSkill.stream()
                .map(skillOffer -> UserSkillGuarantee.builder()
                        .user(skillOffer.getRecommendation().getReceiver())
                        .skill(userSkill)
                        .guarantor(skillOffer.getRecommendation().getAuthor())
                        .build())
                .forEach(userSkillGuaranteeRepository::save);
    }

    public void validateSkill(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException(String.format("Skill with title: %s already exists.", skillDto.getTitle()));
        }
    }

    public void validateMinSkillOffers(long countOffersSkill, long skillId, long userId) {
        if (countOffersSkill < MIN_SKILL_OFFERS) {
            throw new DataValidationException(String.format("Skill with ID: %d hasn't enough offers for user with ID: %d", skillId, userId));
        }
    }
}


