package com.miro.widget.controller.dto;

import java.util.UUID;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateWidgetDTOTest {

    @Test
    @DisplayName("new UpdatedWidgetDTO should throw exception when Id is null")
    void newUpdateWidgetDTO_shouldThrowException_whenIdIsNull() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> new UpdateWidgetDTO(null, 1,2,null, null, null)
        );
    }

    @Test
    @DisplayName("newUpdateWidgetDTO must create a DTO with the widget information")
    void newUpdateWidgetDTO_shouldHoldAllUpdatedWidgetInformation() {
        // given
        final var id = UUID.randomUUID();
        final var coordinateX = 1;
        final var coordinateY = 2;
        final var width = 4;
        final var height = 5;

        // when
        final UpdateWidgetDTO actual = new UpdateWidgetDTO(id, coordinateX, coordinateY,null, width, height);

        // then
        final SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.getId()).isEqualTo(id);
        softAssertions.assertThat(actual.getCoordinateX()).isEqualTo(coordinateX);
        softAssertions.assertThat(actual.getCoordinateY()).isEqualTo(coordinateY);
        softAssertions.assertThat(actual.getzIndex()).isNull();
        softAssertions.assertThat(actual.getHeight()).isEqualTo(height);
        softAssertions.assertThat(actual.getWidth()).isEqualTo(width);
        softAssertions.assertAll();
    }
}