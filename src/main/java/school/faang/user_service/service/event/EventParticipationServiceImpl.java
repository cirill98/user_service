package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventParticipantsDto;
import school.faang.user_service.dto.EventUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.event.EventParticipationValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipationServiceImpl implements EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventParticipationValidator eventParticipationValidator;
    private final UserMapper userMapper;

    public void registerParticipant(long eventId, long userId) {
        eventParticipationValidator.checkEventExists(eventId);
        eventParticipationValidator.checkUserExists(userId);
        eventParticipationValidator.validateParticipationRegistered(eventId, userId);
        eventParticipationRepository.register(eventId, userId);
        log.info("User {} has been registered with event {}", userId, eventId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        eventParticipationValidator.checkEventExists(eventId);
        eventParticipationValidator.checkUserExists(userId);
        eventParticipationValidator.validateParticipationNotRegistered(eventId, userId);
        eventParticipationRepository.unregister(eventId, userId);
        log.info("User {} has been unregistered with event {}", userId, eventId);
    }

    public List<EventUserDto> getParticipants(long eventId) {
        eventParticipationValidator.checkEventExists(eventId);
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        List<EventUserDto> participantsDto = userMapper.usersToUsersDto(participants);
        log.info("Participants of event {}:", eventId);
        return participantsDto;
    }

    public EventParticipantsDto getParticipantsCount(long eventId) {
        eventParticipationValidator.checkEventExists(eventId);
        int count = eventParticipationRepository.countParticipants(eventId);
        log.info("Participant count of event {}:", eventId);
        return new EventParticipantsDto(count);
    }
}