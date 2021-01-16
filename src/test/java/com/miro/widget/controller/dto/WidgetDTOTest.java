package com.miro.widget.controller.dto;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.miro.widget.fixture.WidgetFixture;

@DisplayName("WidgetDTO Test")
class WidgetDTOTest {

    @ParameterizedTest
    @CsvSource(value = {
            ", 1, 2, 3, 4, 5",
            "123e4567-e89b-42d3-a456-556642440000, , 2, 3, 4, 5",
            "123e4567-e89b-42d3-a456-556642440000, 1, , 3, 4, 5",
            "123e4567-e89b-42d3-a456-556642440000, 1, 2, , 4, 5",
            "123e4567-e89b-42d3-a456-556642440000, 1, 2, 3, , 5",
            "123e4567-e89b-42d3-a456-556642440000, 1, 2, 3, 4, ",
    })
    @DisplayName("new Widget should throw exception when one of the fields is null")
    void newWidgetDTO_shouldThrowException_whenOneFieldIsNull(
            final UUID id,
            final Integer coordinateX,
            final Integer coordinateY,
            final Integer zIndex,
            final Integer width,
            final Integer height
    ) {
        assertThrows(
                NullPointerException.class,
                () -> new WidgetDTO(id, ZonedDateTime.now(), coordinateX, coordinateY, zIndex, width, height)
        );
    }

    @Test
    @DisplayName("new Widget should throw exception when Last Modification Date is null")
    void newWidgetDTO_shouldThrowException_whenLastModificationDateFieldIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new WidgetDTO(UUID.randomUUID(), null, 1, 2, 3, 4, 5)
        );
    }

    @Test
    @DisplayName("from must create a DTO with the widget information")
    void from_shouldCreateWidgetDTO() {
        // given
        final var widget = WidgetFixture.create();

        // when
        final WidgetDTO actual = WidgetDTO.from(widget);

        // then
        final SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.getId()).isEqualTo(widget.getId());
        softAssertions.assertThat(actual.getLastModificationDate()).isEqualTo(widget.getLastModificationDate());
        softAssertions.assertThat(actual.getCoordinateX()).isEqualTo(widget.getCoordinateX());
        softAssertions.assertThat(actual.getCoordinateY()).isEqualTo(widget.getCoordinateY());
        softAssertions.assertThat(actual.getzIndex()).isEqualTo(widget.getZIndex());
        softAssertions.assertThat(actual.getHeight()).isEqualTo(widget.getHeight());
        softAssertions.assertThat(actual.getWidth()).isEqualTo(widget.getWidth());
        softAssertions.assertAll();
    }
}