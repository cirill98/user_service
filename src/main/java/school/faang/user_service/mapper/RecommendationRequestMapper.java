package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {
    @Mapping(source = "skills", target = "skillsId", qualifiedByName = "mapSkills")
    @Mapping(source = "requester", target = "requesterId", qualifiedByName = "mapRequester")
    @Mapping(source = "receiver", target = "recieverId", qualifiedByName = "mapReceiver")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Named("mapSkills")
    default List<Long> mapSkills(List<SkillRequest> skillRequests) {
        return skillRequests.stream().map(SkillRequest::getId).toList();
    }

    @Named("mapRequester")
    default long mapRequester(User user) {
        return user.getId();
    }

    @Named("mapReceiver")
    default long mapReceiver(User user) {
        return user.getId();
    }
}