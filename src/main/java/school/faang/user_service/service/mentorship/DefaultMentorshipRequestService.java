package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filter.mentorship.MentorshipRequestFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class DefaultMentorshipRequestService implements MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        var receiverId = mentorshipRequestDto.getReceiverId();
        var requesterId = mentorshipRequestDto.getRequesterId();
        validateSelfMentorship(receiverId, requesterId);
        validateUsersExistence(receiverId, requesterId);
        validateEligibilityForMentorship(requesterId, receiverId);
        mentorshipRequestDto.setRequestStatus(RequestStatus.PENDING);
        return mapper.toDto(mentorshipRequestRepository.save(mapper.toEntity(mentorshipRequestDto)));
    }

    @Override
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        var mentorshipRequests = StreamSupport.stream(mentorshipRequestRepository.findAll().spliterator(), false);
        return mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(mentorshipRequests, filters))
                .map(mapper::toDto)
                .toList();
    }

    private void validateSelfMentorship(Long receiverId, Long requesterId) {
        if (Objects.equals(receiverId, requesterId)) {
            throw new IllegalArgumentException(ExceptionMessages.SELF_MENTORSHIP);
        }
    }

    private void validateUsersExistence(Long receiverId, Long requesterId) {
        if (!userRepository.existsById(receiverId)) {
            throw new NoSuchElementException(ExceptionMessages.RECEIVER_NOT_FOUND);
        }
        if (!userRepository.existsById(requesterId)) {
            throw new NoSuchElementException(ExceptionMessages.REQUESTER_NOT_FOUND);
        }
    }

    private void validateEligibilityForMentorship(Long requesterId, Long receiverId) {
        mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)
                .map(MentorshipRequest::getCreatedAt)
                .ifPresent(creationDate -> {
                    if (creationDate.isAfter(LocalDateTime.now().minusMonths(3))) {
                        throw new IllegalStateException(ExceptionMessages.MENTORSHIP_FREQUENCY);
                    }
                });
    }
}