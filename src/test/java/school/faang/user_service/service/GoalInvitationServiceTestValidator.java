package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.filter.TestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static school.faang.user_service.exception.MessageForGoalInvitationService.*;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTestValidator {
    @InjectMocks
    private GoalInvitationServiceValidator goalInvitationServiceValidator;
    private final TestData testData = new TestData();

    @Test
    void testValidateForCreateInvitationWithInputIsNull() {

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationServiceValidator.validateForCreateInvitation(null));

        assertEquals(INPUT_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForCreateInvitationWhenInviterIdIsNull() {

        GoalInvitationDto goalInvitationDto = testData.setupForCreateInvitation();
        goalInvitationDto.setInviterId(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationServiceValidator.validateForCreateInvitation(goalInvitationDto));

        assertEquals(INVITER_ID_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForCreateInvitationWhenInvitedUserIdIsNull() {

        GoalInvitationDto goalInvitationDto = testData.setupForCreateInvitation();
        goalInvitationDto.setInvitedUserId(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationServiceValidator.validateForCreateInvitation(goalInvitationDto));

        assertEquals(INVITED_USER_ID_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForCreateInvitationWhenInvitedUserIdEqualsInviterId() {

        GoalInvitationDto goalInvitationDto = testData.setupForCreateInvitation();
        goalInvitationDto.setInvitedUserId(25L);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationServiceValidator.validateForCreateInvitation(goalInvitationDto));

        assertEquals(INVITER_ID_EQUALS_INVITED_USER_ID.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForAcceptGoalInvitationWithNoInvited() {
        GoalInvitation goalInvitation = testData.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationServiceValidator.validateForAcceptGoalInvitation(null, goalInvitation.getGoal()));

        assertEquals(NO_INVITED_IN_GOAL_INVITATION.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForAcceptGoalInvitationIfSetGoalsIsNull() {
        GoalInvitation goalInvitation = testData.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        goalInvitation.getInvited().setSetGoals(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationServiceValidator.validateForAcceptGoalInvitation(goalInvitation.getInvited(), goalInvitation.getGoal()));

        assertEquals(SET_GOALS_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForAcceptGoalInvitationIfSetGoalsMoreThanThree() {
        GoalInvitation goalInvitation = testData.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        List<Goal> setGoals = goalInvitation.getInvited().getSetGoals();
        setGoals.add(new Goal());
        setGoals.add(new Goal());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationServiceValidator.validateForAcceptGoalInvitation(goalInvitation.getInvited(), goalInvitation.getGoal()));

        assertEquals(MORE_THEN_THREE_GOALS.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForAcceptGoalInvitationSetGoalsWithoutCertainGoal() {
        GoalInvitation goalInvitation = testData.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationServiceValidator.validateForAcceptGoalInvitation(goalInvitation.getInvited(), goalInvitation.getGoal()));

        assertEquals(INVITED_HAS_GOAL.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateGetInvitationsWithFilterIsNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalInvitationServiceValidator.validateForGetInvitations(null));
        assertEquals(INPUT_IS_NULL.getMessage(), exception.getMessage());
    }
}
