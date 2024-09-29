package school.faang.user_service.service.goal.invitation.invitation_filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InvitationInviterIdFilter implements InvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto invitationFilterDto) {
        return invitationFilterDto.getInviterId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto invitationFilterDto) {
        return invitations.filter(goalInvitation ->
                goalInvitation.getInviter().getId().equals(invitationFilterDto.getInviterId()));
    }
}
