package com.miro.widget.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.miro.widget.fixture.WidgetFixture;
import com.miro.widget.model.Widget;
import com.miro.widget.repository.WidgetRepository;
import com.miro.widget.util.Page;

@DisplayName("Widget Service Test")
class WidgetServiceTest {

    private final WidgetRepository widgetRepositoryMock = mock(WidgetRepository.class);

    private final WidgetService subject = new WidgetService(widgetRepositoryMock);

    @Test
    @DisplayName("createWidget saves a new widget in the empty repository there is not need to shift widgets")
    void createWidget_shouldSaveOnlyNewWidget_whenRepositoryIsEmpty() {
        // given
        final var coordinateX = 37;
        final var coordinateY = 48;
        final var zIndex = 2;
        final var width = 10;
        final var height = 12;

        when(widgetRepositoryMock.findByZIndex(zIndex)).thenReturn(Optional.empty());

        // when
        final Widget actual = subject.createWidget(coordinateX, coordinateY, zIndex, width, height);

        // then
        verify(widgetRepositoryMock).saveAll(Set.of(actual));
        final SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.getId()).isNotNull();
        softAssertions.assertThat(actual.getLastModificationDate()).isNotNull();
        softAssertions.assertThat(actual.getCoordinateX()).isEqualTo(coordinateX);
        softAssertions.assertThat(actual.getCoordinateY()).isEqualTo(coordinateY);
        softAssertions.assertThat(actual.getZIndex()).isEqualTo(zIndex);
        softAssertions.assertThat(actual.getWidth()).isEqualTo(width);
        softAssertions.assertThat(actual.getHeight()).isEqualTo(height);
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("createWidget saves a new widget without zIndex in repository uses next zIndex from repository")
    void createWidget_shouldSaveNewWidgetWithNextZIndex_whenZIndexIsNotAssigned() {
        // given
        final var coordinateX = 37;
        final var coordinateY = 48;
        final var zIndex = 2;
        final var width = 10;
        final var height = 12;

        when(widgetRepositoryMock.findNextZIndex()).thenReturn(zIndex);
        when(widgetRepositoryMock.findByZIndex(zIndex)).thenReturn(Optional.empty());

        // when
        final Widget actual = subject.createWidget(coordinateX, coordinateY, null, width, height);

        // then
        verify(widgetRepositoryMock).saveAll(Set.of(actual));
        assertThat(actual.getZIndex()).isEqualTo(zIndex);
    }

    @Test
    @DisplayName("createWidget saves a new widget with duplicated zIndex in repository shift widget until find a gap")
    @SuppressWarnings("unchecked")
    void createWidget_shouldShiftZIndexUntilFindAGap_whenWidgetHasDuplicatedZIndex() {
        // given
        final var coordinateX = 37;
        final var coordinateY = 48;
        final var zIndex = 2;
        final var width = 10;
        final var height = 12;

        final var widget2 = WidgetFixture.create(zIndex);
        final var widget3 = WidgetFixture.create(3);
        final var zIndexGap = 4;
        final var widget5 = WidgetFixture.create(5);
        when(widgetRepositoryMock.findByZIndex(widget2.getZIndex())).thenReturn(Optional.of(widget2));
        when(widgetRepositoryMock.findByZIndex(widget3.getZIndex())).thenReturn(Optional.of(widget3));
        when(widgetRepositoryMock.findByZIndex(zIndexGap)).thenReturn(Optional.empty());
        when(widgetRepositoryMock.findByZIndex(widget5.getZIndex())).thenReturn(Optional.of(widget5));

        // when
        final Widget actual = subject.createWidget(coordinateX, coordinateY, zIndex, width, height);

        // then
        final ArgumentCaptor<Set<Widget>> widgetArgumentCaptor = ArgumentCaptor.forClass(Set.class);
        verify(widgetRepositoryMock).saveAll(widgetArgumentCaptor.capture());
        final Set<Widget> savedWidgets = widgetArgumentCaptor.getValue();
        assertSaved(savedWidgets, actual, zIndex);
        assertSaved(savedWidgets, widget2, 3);
        assertSaved(savedWidgets, widget3, 4);
        assertThat(savedWidgets).doesNotContain(widget5);
    }

    private void assertSaved(final Set<Widget> savedWidgets, final Widget actual, final int zIndex) {
        assertThat(
                savedWidgets.stream()
                .filter(w -> w.getId().equals(actual.getId()))
                .findFirst()
        ).isPresent()
                .hasValueSatisfying(w -> assertThat(w.getZIndex()).isEqualTo(zIndex));
    }

    @Test
    @DisplayName("updateWidget throws WidgetNotFoundException when widget is not found")
    void updateWidget_shouldThrowException_whenWidgetNotFound() {
        // given
        final var widgetId = UUID.randomUUID();

        when(widgetRepositoryMock.findById(widgetId)).thenReturn(Optional.empty());

        // when - then
        assertThrows(
                WidgetService.WidgetNotFoundException.class,
                () -> subject.updateWidget(widgetId, null, null, null, null, null),
                String.format("Widget %s not found", widgetId)
        );
    }

    @Test
    @DisplayName("updateWidget updated only zIndex when all the value passed as input are null")
    void updateWidget_shouldUpdateZIndexWithMaxValue_whenAllUpdateArgAreNull() {
        final var widget = WidgetFixture.create();
        final UUID widgetId = widget.getId();
        when(widgetRepositoryMock.findById(widgetId)).thenReturn(Optional.of(widget));

        final var newZIndex = 2;
        when(widgetRepositoryMock.findNextZIndex()).thenReturn(newZIndex);

        when(widgetRepositoryMock.findByZIndex(newZIndex)).thenReturn(Optional.empty());

        // when
        final Widget actual = subject.updateWidget(widgetId, null, null, null, null, null);

        // then
        verify(widgetRepositoryMock).saveAll(anySet());

        final SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.getId()).isEqualTo(widgetId);
        softAssertions.assertThat(actual.getLastModificationDate()).isNotNull();
        softAssertions.assertThat(actual.getCoordinateX()).isEqualTo(widget.getCoordinateX());
        softAssertions.assertThat(actual.getCoordinateY()).isEqualTo(widget.getCoordinateY());
        softAssertions.assertThat(actual.getZIndex()).isEqualTo(newZIndex);
        softAssertions.assertThat(actual.getWidth()).isEqualTo(widget.getWidth());
        softAssertions.assertThat(actual.getHeight()).isEqualTo(widget.getHeight());
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("updateWidget updates all non null input parameters when it is passed to update")
    void updateWidget_shouldUpdateAllParameters_whenTheyAreNotNull() {
        final var widget = WidgetFixture.create();
        final UUID widgetId = widget.getId();
        when(widgetRepositoryMock.findById(widgetId)).thenReturn(Optional.of(widget));

        final int zIndex = widget.getZIndex();
        when(widgetRepositoryMock.findByZIndex(zIndex)).thenReturn(Optional.empty());

        final int newCoordinateX = widget.getCoordinateX() + 1;
        final int newCoordinateY = widget.getCoordinateY() + 1;
        final int newHeight = widget.getHeight() + 1;
        final int newWidth = widget.getWidth() + 1;

        // when
        final Widget actual = subject.updateWidget(widgetId, newCoordinateX, newCoordinateY, zIndex, newWidth, newHeight);

        // then

        verify(widgetRepositoryMock).saveAll(anySet());
        
        final SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.getId()).isEqualTo(widgetId);
        softAssertions.assertThat(actual.getLastModificationDate()).isNotNull();
        softAssertions.assertThat(actual.getCoordinateX()).isEqualTo(newCoordinateX);
        softAssertions.assertThat(actual.getCoordinateY()).isEqualTo(newCoordinateY);
        softAssertions.assertThat(actual.getZIndex()).isEqualTo(zIndex);
        softAssertions.assertThat(actual.getWidth()).isEqualTo(newWidth);
        softAssertions.assertThat(actual.getHeight()).isEqualTo(newHeight);
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("createWidget saves a new widget with duplicated zIndex in repository shift widget until find a gap")
    @SuppressWarnings("unchecked")
    void updateWidget_shouldShiftWidget_whenZIndexIsUpdateToDuplicatedOne() {
        // given
        final var widget1 = WidgetFixture.create(1);
        final var zIndex = 2;
        final var widget2 = WidgetFixture.create(zIndex);
        final var widget3 = WidgetFixture.create(3);
        final var zIndexGap = 4;
        final var widget5 = WidgetFixture.create(5);

        when(widgetRepositoryMock.findById(widget1.getId())).thenReturn(Optional.of(widget1));

        when(widgetRepositoryMock.findByZIndex(widget2.getZIndex())).thenReturn(Optional.of(widget2));
        when(widgetRepositoryMock.findByZIndex(widget3.getZIndex())).thenReturn(Optional.of(widget3));
        when(widgetRepositoryMock.findByZIndex(zIndexGap)).thenReturn(Optional.empty());
        when(widgetRepositoryMock.findByZIndex(widget5.getZIndex())).thenReturn(Optional.of(widget5));

        // when
        subject.updateWidget(
                widget1.getId(),
                widget1.getCoordinateX(),
                widget1.getCoordinateY(),
                zIndex,
                widget1.getWidth(),
                widget1.getHeight()
        );

        // then
        final ArgumentCaptor<Set<Widget>> widgetArgumentCaptor = ArgumentCaptor.forClass(Set.class);
        verify(widgetRepositoryMock).saveAll(widgetArgumentCaptor.capture());
        final Set<Widget> savedWidgets = widgetArgumentCaptor.getValue();
        assertSaved(savedWidgets, widget1, zIndex);
        assertSaved(savedWidgets, widget2, 3);
        assertSaved(savedWidgets, widget3, 4);
        assertThat(savedWidgets).doesNotContain(widget5);
    }

    @Test
    @DisplayName("deleteWidget should delete widget from repository based on id")
    void deleteWidget_shouldDeleteWidget() {
        // given
        final var widgetId = UUID.randomUUID();

        // when
        subject.deleteWidget(widgetId);

        // then
        verify(widgetRepositoryMock).delete(eq(widgetId));
    }

    @Test
    @DisplayName("findAllOrderByZIndex returns list of widgets from repository")
    void findAllOrderByZIndex_shouldReturnListOfWidget_whenWidgetsAreFoundInRepository() {
        // given
        final var widget = WidgetFixture.create();
        when(widgetRepositoryMock.findAllOrderedByZIndex()).thenReturn(List.of(widget));

        // when
        final List<Widget> actual = subject.findAllOrderByZIndex();

        // then
        assertThat(actual).hasSize(1).containsExactlyInAnyOrder(widget);
        verify(widgetRepositoryMock).findAllOrderedByZIndex();
    }

    @Test
    @DisplayName("findById returns empty when widget id in not found")
    void findById_shouldReturnsWidget_whenWidgetIsFoundInRepository() {
        // given
        final var widget = WidgetFixture.create();
        when(widgetRepositoryMock.findById(widget.getId())).thenReturn(Optional.of(widget));

        // when
        final Optional<Widget> actual = subject.findById(widget.getId());

        // then
        assertThat(actual).isPresent().hasValue(widget);
    }

    @Test
    @DisplayName("findAllOrderByZIndex with Page returns the Widget that fits in one page based on repository result")
    void findAllOrderByZIndex_shouldReturnsWidgets_whenWidgetsAreFoundInRepository() {
        // given
        final var widget1 = WidgetFixture.create(1);
        final var widget2 = WidgetFixture.create(2);
        final var page = new Page(0, 5);

        when(widgetRepositoryMock.findAllOrderedByZIndex(page)).thenReturn(List.of(widget1, widget2));

        // when
        final List<Widget> actual = subject.findAllOrderByZIndex(page);

        // then
        assertThat(actual).hasSize(2).containsExactly(widget1, widget2);
    }

}