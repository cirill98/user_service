package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InvitedNamePatternFilterTest {
    @InjectMocks
    private InvitedNamePatternFilter invitedNamePatternFilter;
    private InvitationFilterDto invitationFilterDto;
    private TestData testData;

    @BeforeEach
    void prepareInvitationFilterDto() {
        testData = new TestData();
        invitationFilterDto = testData.prepareInvitationFilterDto();
    }


    @Test
    void testIsApplicableFalse() {
        invitationFilterDto.setInvitedNamePattern(null);
        assertFalse(invitedNamePatternFilter.isApplicable(invitationFilterDto));
    }

    @Test
    void testIsApplicableTrue() {
        assertTrue(invitedNamePatternFilter.isApplicable(invitationFilterDto));
    }

    @Test
    void testApplyWithGoalInvitation() {
        assertEquals(1, invitedNamePatternFilter.apply(testData.prepareGoalInvitationList().stream(),
                invitationFilterDto).toList().size());
    }

    @Test
    void testApplyWithoutGoalInvitation() {
        invitationFilterDto.setInvitedNamePattern("Kelly");
        assertEquals(0, invitedNamePatternFilter.apply(testData.prepareGoalInvitationList().stream(), invitationFilterDto).toList().size());
    }
}
