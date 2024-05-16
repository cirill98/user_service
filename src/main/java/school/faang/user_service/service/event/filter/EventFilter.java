package school.faang.user_service.service.event.filter;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public interface EventFilter {
    boolean isAcceptable(EventFilterDto filterDto);

    Stream<Event> apply(Stream<Event> events, EventFilterDto filters);
}