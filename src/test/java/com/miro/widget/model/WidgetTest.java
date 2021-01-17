package com.miro.widget.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.miro.widget.fixture.WidgetFixture;

@DisplayName("Widget Test")
class WidgetTest {

    @Test
    @DisplayName("should not be possible to create a Widget without id")
    void build_shouldThrowException_whenIdIsNull() {
        // given
        final Widget.WidgetBuilder builder = Widget.builder()
                .setCoordinateX(1)
                .setCoordinateY(2)
                .setZIndex(0)
                .setWidth(4)
                .setHeight(5);

        // when - then
        assertThrows(
                NullPointerException.class,
                builder::build
        );
    }

    @Test
    @DisplayName("should not be possible to create a Widget without X")
    void build_shouldThrowException_whenCoordinateXIsNull() {
        // given
        final Widget.WidgetBuilder builder = Widget.builder()
                .setId(UUID.randomUUID())
                .setCoordinateY(2)
                .setZIndex(0)
                .setWidth(4)
                .setHeight(5);

        // when - then
        assertThrows(
                NullPointerException.class,
                builder::build
        );
    }

    @Test
    @DisplayName("should not be possible to create a Widget without Y")
    void build_shouldThrowException_whenCoordinateYIsNull() {
        // given
        final Widget.WidgetBuilder builder = Widget.builder()
                .setId(UUID.randomUUID())
                .setCoordinateX(1)
                .setZIndex(0)
                .setWidth(4)
                .setHeight(5);

        // when - then
        assertThrows(
                NullPointerException.class,
                builder::build
        );
    }

    @Test
    @DisplayName("should not be possible to create a Widget without Z-Index")
    void build_shouldThrowException_whenZIndexIsNull() {
        // given
        final Widget.WidgetBuilder builder = Widget.builder()
                .setId(UUID.randomUUID())
                .setCoordinateX(1)
                .setCoordinateY(2)
                .setWidth(4)
                .setHeight(5);

        // when - then
        assertThrows(
                NullPointerException.class,
                builder::build
        );
    }

    @Test
    @DisplayName("should not be possible to create a Widget without width")
    void build_shouldThrowException_whenWidthIsNull() {
        // given
        final Widget.WidgetBuilder builder = Widget.builder()
                .setId(UUID.randomUUID())
                .setCoordinateX(1)
                .setCoordinateY(2)
                .setZIndex(0)
                .setHeight(5);

        // when - then
        assertThrows(
                NullPointerException.class,
                builder::build
        );
    }

    @ParameterizedTest
    @CsvSource({"0", "-1"})
    @DisplayName("should not be possible to create a Widget with With negative or Zero")
    void build_shouldThrowException_whenWidthIsNegative(final int width) {
        // given
        final Widget.WidgetBuilder builder = Widget.builder()
                .setId(UUID.randomUUID())
                .setCoordinateX(1)
                .setCoordinateY(2)
                .setZIndex(0)
                .setWidth(width)
                .setHeight(5);

        // when - then
        assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
    }

    @Test
    @DisplayName("should not be possible to create a Widget without height")
    void build_shouldThrowException_whenWithoutHeight() {
        // given
        final Widget.WidgetBuilder builder = Widget.builder()
                .setId(UUID.randomUUID())
                .setCoordinateX(1)
                .setCoordinateY(2)
                .setZIndex(0)
                .setWidth(4);

        // when - then
        assertThrows(
                NullPointerException.class,
                builder::build
        );
    }

    @ParameterizedTest
    @CsvSource({"0", "-1"})
    @DisplayName("should not be possible to create a Widget with negative or zero height")
    void build_shouldThrowException_whenHeightIsInvalid(final int height) {
        // given
        final Widget.WidgetBuilder builder = Widget.builder()
                .setId(UUID.randomUUID())
                .setCoordinateX(1)
                .setCoordinateY(2)
                .setZIndex(0)
                .setWidth(4)
                .setHeight(height);

        // when - then
        assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
    }

    @Test
    @DisplayName("toBuilder creates a copy of object updating last modification date")
    void toBuilder_shouldUpdateLastModificationDate_whenCreatesAWidgetCopy() {
        // given
        final var widget = WidgetFixture.create();

        // when
        final var actual = widget.toBuilder().build();

        // then
        assertThat(actual.getLastModificationDate()).isAfter(widget.getLastModificationDate());
    }

}