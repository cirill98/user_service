package school.faang.user_service.messaging.publisher.mentorship.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.event.mentorship.request.MentorshipRequestedEvent;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.messaging.publisher.EventPublisher;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipRequestedEventPublisher implements EventPublisher<MentorshipRequestedEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipRequestedTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(MentorshipRequestedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(mentorshipRequestedTopic.getTopic(), message);
            log.info("Published MentorshipRequested event: {}", message);
        } catch (JsonProcessingException e) {
            log.error(ExceptionMessages.SERIALIZATION_ERROR + event, e);
            throw new IllegalArgumentException(ExceptionMessages.SERIALIZATION_ERROR + event, e);
        } catch (Exception e) {
            log.error(ExceptionMessages.UNEXPECTED_ERROR + e.getMessage());
            throw new IllegalArgumentException(ExceptionMessages.UNEXPECTED_ERROR + e.getMessage());
        }
    }
}