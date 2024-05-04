package school.faang.user_service.service.event.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.service.event.filter.TestData.ALL_EVENTS;

class EventSkillFilterTest {
    private final EventSkillFilter eventSkillFilter = new EventSkillFilter();
    private EventFilterDto filter;
    private Stream<Event> eventsToFilter;
    private Stream<Event> expectedFilteredEvents;

    @BeforeEach
    void setUp() {
        eventsToFilter = ALL_EVENTS.stream();

        filter = new EventFilterDto();
        filter.setSkillPattern("SQL");

        expectedFilteredEvents = ALL_EVENTS.stream();
    }

    @Nested
    class positiveTests {
        @DisplayName("should return true when pattern isn't empty")
        @Test
        void shouldReturnTrueWhenPatternIsntEmpty() {
            var isApplicable = eventSkillFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return filtered events")
        @Test
        void shouldReturnFilteredEvents() {
            var actualFilteredUsers = eventSkillFilter.apply(eventsToFilter, filter);

            assertEquals(expectedFilteredEvents.toList(), actualFilteredUsers.toList());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should return false when empty pattern is passed")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        void shouldReturnFalseWhenPatternIsEmpty(String pattern) {
            filter.setSkillPattern(pattern);

            var isApplicable = eventSkillFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return empty list when no one event matched passed filter")
        @Test
        void shouldReturnEmptyListWhenNothingMatchedFilter() {
            filter.setSkillPattern("C++");

            var actualFilteredUsers = eventSkillFilter.apply(eventsToFilter, filter);

            assertEquals(List.of(), actualFilteredUsers.toList());
        }
    }
}