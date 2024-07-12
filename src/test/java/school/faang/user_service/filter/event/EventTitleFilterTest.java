package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EventTitleFilterTest {

    private final LocalDateTime DATE_NOW = LocalDateTime.now();

    @Test
    void apply() {
        LocalDateTime fake_date = LocalDateTime.now();
        EventFilterDto eventFilterDto = new EventFilterDto("title", fake_date);

        boolean actual = new EventTitleFilter().apply(getEvent(), eventFilterDto);

        assertTrue(actual);
    }

    private Event getEvent() {
        return Event.builder()
                .title("title")
                .startDate(DATE_NOW)
                .endDate(DATE_NOW.plusDays(1))
                .owner(getUser(1L))
                .description("description")
                .relatedSkills(Stream.of(1L, 2L)
                        .map(this::getSkill)
                        .toList())
                .type(EventType.POLL)
                .status(EventStatus.PLANNED)
                .maxAttendees(1)
                .build();
    }

    private Skill getSkill(Long id) {
        return Skill.builder()
                .id(id)
                .build();
    }

    private User getUser(Long id) {
        return User.builder()
                .id(id)
                .build();
    }
}