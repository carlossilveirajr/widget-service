package com.miro.widget.controller.dto;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("CreateWidgetDTO Test")
class CreateWidgetDTOTest {

    @ParameterizedTest
    @CsvSource(value = {
            " , 2, 3, 4, 5",
            "1, , 3, 4, 5",
            "1, 2, , , 5",
            "1, 2, , 4, ",
    })
    @DisplayName("new CreateWidgetDTO should throw exception when one of the fields is null")
    void newCreateWidgetDTO_shouldThrowException_whenOneFieldIsNull(
            final Integer coordinateX,
            final Integer coordinateY,
            final Integer zIndex,
            final Integer width,
            final Integer height
    ) {
        assertThrows(
                NullPointerException.class,
                () -> new CreateWidgetDTO(coordinateX, coordinateY, zIndex, width, height)
        );
    }

    @Test
    @DisplayName("newCreateWidgetDTO must create a DTO with the widget information")
    void newCreateWidgetDTO_shouldHoldAllUpdatedWidgetInformation() {
        // given
        final var coordinateX = 1;
        final var coordinateY = 2;
        final var width = 4;
        final var height = 5;

        // when
        final CreateWidgetDTO actual = new CreateWidgetDTO(coordinateX, coordinateY,null, width, height);

        // then
        final SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.getCoordinateX()).isEqualTo(coordinateX);
        softAssertions.assertThat(actual.getCoordinateY()).isEqualTo(coordinateY);
        softAssertions.assertThat(actual.getzIndex()).isNull();
        softAssertions.assertThat(actual.getHeight()).isEqualTo(height);
        softAssertions.assertThat(actual.getWidth()).isEqualTo(width);
        softAssertions.assertAll();
    }

}