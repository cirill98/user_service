package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.EntityNotFoundException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Bulgakov
 */
@Service
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final GoalInvitationValidator goalInvitationValidator;
    private final UserService userService;
    private final GoalService goalService;
    private final List<Filter<InvitationFilterDto, GoalInvitation>> goalInvitationFilters;

    public GoalInvitation getGoalInvitationById(long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("GoalInvitation by id: " + id + " is not found"));
    }

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        goalInvitationValidator.checkUser(invitation.getInviterId(), invitation.getInvitedUserId());

        User inviter = userService.getUserById(invitation.getInviterId());
        User invited = userService.getUserById(invitation.getInvitedUserId());

        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(invitation);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);

        goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toDto(goalInvitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = getGoalInvitationById(id);

        User invitedUser = goalInvitation.getInvited();

        goalInvitationValidator.validateGoal(invitedUser, goalInvitation);

        List<Goal> currentUserGoals = invitedUser.getGoals();
        currentUserGoals.add(goalInvitation.getGoal());

        invitedUser.setGoals(currentUserGoals);

        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toDto(goalInvitation);
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = getGoalInvitationById(id);
        goalService.existsGoalById(goalInvitation.getGoal().getId());

        User invited = goalInvitation.getInvited();

        List<GoalInvitation> currentReceivedGoalInvitations = invited.getReceivedGoalInvitations();
        currentReceivedGoalInvitations.remove(goalInvitation);

        invited.setReceivedGoalInvitations(currentReceivedGoalInvitations);
        goalInvitation.setStatus(RequestStatus.REJECTED);
        userService.saveUser(invited);
        goalInvitationRepository.save(goalInvitation);

        return goalInvitationMapper.toDto(goalInvitation);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        List<GoalInvitation> goalInvitations = goalInvitationRepository.findAll();

        if (!goalInvitationValidator.checkFilter(filter)) {
            return new ArrayList<>(goalInvitations.stream().map(goalInvitationMapper::toDto).toList());
        }

        goalInvitationFilters.stream()
                .filter(goalInvitationFilter -> goalInvitationFilter.isApplicable(filter))
                .forEach(goalInvitationFilter -> goalInvitationFilter.apply(goalInvitations, filter));

        return new ArrayList<>(goalInvitations.stream()
                .map(goalInvitationMapper::toDto).toList());
    }
}
