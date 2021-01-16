package com.miro.widget.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.miro.widget.fixture.WidgetFixture;

@DisplayName("Widget Test")
class WidgetTest {

    @Test
    @DisplayName("Widget should not be created with null id")
    void widget_shouldThrowException_whenIdIsNull() {
        // when - then
        assertThrows(
                NullPointerException.class,
                () -> new Widget(null, ZonedDateTime.now(), 1, 2, 3, 4, 5)
        );
    }

    @Test
    @DisplayName("Widget should not be created with null last modification date")
    void widget_shouldThrowException_whenLastModificationDateIsNull() {
        // when - then
        assertThrows(
                NullPointerException.class,
                () -> new Widget(UUID.randomUUID(), null, 1, 2, 3, 4, 5)
        );
    }

    @Test
    @DisplayName("Widget should not be created with negative height")
    void widget_shouldThrowException_whenCreatingWidgetWithNegativeHeight() {
        // when - then
        assertThrows(
                IllegalArgumentException.class,
                () -> new Widget(UUID.randomUUID(), ZonedDateTime.now(), 1, 2, 3, 4, -5)
        );
    }

    @Test
    @DisplayName("Widget should not be created with negative width")
    void widget_shouldThrowException_whenCreatingWidgetWithNegativeWidth() {
        // when - then
        assertThrows(
                IllegalArgumentException.class,
                () -> new Widget(UUID.randomUUID(), ZonedDateTime.now(), 1, 2, 3, -4, 5)
        );
    }

    @Test
    @DisplayName("Widget should not set height as negative value")
    void widget_shouldThrowException_whenHeightIsNegative() {
        // given
        final Widget widget = WidgetFixture.create();

        // when - then
        assertThrows(
                IllegalArgumentException.class,
                () -> widget.setHeight(-1)
        );
    }

    @Test
    @DisplayName("Widget should not set width as negative value")
    void widget_shouldThrowException_whenWidthIsNegative() {
        // given
        final Widget widget = WidgetFixture.create();

        // when - then
        assertThrows(
                IllegalArgumentException.class,
                () -> widget.setWidth(-1)
        );
    }

}