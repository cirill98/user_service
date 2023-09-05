package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestedEventDto;

@Component
public class MentorshipRequestedEventPublisher extends AbstractPublisher<MentorshipRequestedEventDto> {

    public MentorshipRequestedEventPublisher(ObjectMapper objectMapper,
                                             RedisTemplate<String, Object> redisTemplate,
                                             @Value("${spring.data.redis.channels.mentorship_requested_channel.name}")
                                             String topic) {
        super(objectMapper, redisTemplate, topic);
    }
}
