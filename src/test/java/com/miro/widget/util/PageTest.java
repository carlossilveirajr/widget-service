package com.miro.widget.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Page Test")
class PageTest {

    @Test
    @DisplayName("new should create a page with positive size and page values")
    void newPage_shouldCreateAPage() {
        // given
        final int size = 3;
        final int page = 0;

        // when
        final Page actual = new Page(page, size);

        // then
        assertThat(actual.getSize()).isEqualTo(size);
        assertThat(actual.getPage()).isEqualTo(page);
    }


    @ParameterizedTest
    @CsvSource({"0, 5", "-1, 5", "1, -1"})
    @DisplayName("new page is not allowed to have invalid parameters")
    void newPage_shouldThrowException_whenParametersAreInvalid(final int size, final int page) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Page(page, size)
        );
    }
}