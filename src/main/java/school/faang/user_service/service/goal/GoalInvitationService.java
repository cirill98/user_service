package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */
@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper invitationMapper;
    private final GoalInvitationValidator goalInvitationValidator;
    private final UserService userService;

    public GoalInvitation getGoalInvitationById(long id) throws EntityNotFoundException {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("GoalInvitation by id: " + id + " is not found"));
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = null;
        try {
            goalInvitation = getGoalInvitationById(id);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }

        User invitedUser = goalInvitation.getInvited();

        goalInvitationValidator.validateGoal(invitedUser, goalInvitation);

        List<Goal> currentUserGoals = invitedUser.getGoals();
        currentUserGoals.add(goalInvitation.getGoal());

        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        goalInvitationRepository.save(goalInvitation);

        return invitationMapper.toDto(goalInvitation);
    }
}
