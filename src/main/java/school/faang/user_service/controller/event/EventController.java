package school.faang.user_service.controller.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static school.faang.user_service.exception.ExceptionMessage.INVALID_EVENT_START_DATE_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NULL_EVENT_FILTER_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NULL_EVENT_OWNER_ID_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NULL_OR_BLANK_EVENT_TITLE_EXCEPTION;

@Controller
@AllArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto event) {
        validateEvent(event);

        return eventService.create(event);
    }

    public EventDto getEvent(long eventId) {
        return eventService.getEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        if (filter == null) {
            throw new DataValidationException(NULL_EVENT_FILTER_EXCEPTION.getMessage());
        }

        return eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(EventDto event) {
        validateEvent(event);

        return eventService.updateEvent(event);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    private void validateEvent(EventDto event) {

        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new DataValidationException(NULL_OR_BLANK_EVENT_TITLE_EXCEPTION.getMessage());
        }

        if (event.getStartDate() == null || event.getStartDate().isBefore(LocalDateTime.now())) {
            throw new DataValidationException(INVALID_EVENT_START_DATE_EXCEPTION.getMessage());
        }

        if (event.getOwnerId() == null) {
            throw new DataValidationException(NULL_EVENT_OWNER_ID_EXCEPTION.getMessage());
        }
    }
}