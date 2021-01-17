package com.miro.widget.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.miro.widget.model.Widget;
import com.miro.widget.fixture.WidgetFixture;

@DisplayName("In Memory Widget Repository Test")
class InMemoryWidgetRepositoryTest {

    private final WidgetRepository subject = new InMemoryWidgetRepository();

    @BeforeEach
    void setUp() {
        subject.deleteAll();
    }

    @Test
    @DisplayName("findById returns empty when the repository has no widget")
    void findById_shouldReturnsEmpty_whenWidgetRepositoryIsEmpty() {
        // given
        final var widgetId = UUID.randomUUID();

        // when
        final Optional<Widget> actual = subject.findById(widgetId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("findById returns empty when widget is not found")
    void findById_shouldReturnsEmpty_whenWidgetIsNotFound() {
        // given
        subject.saveAll(Set.of(WidgetFixture.create()));

        final var widgetId = UUID.randomUUID();

        // when
        final Optional<Widget> actual = subject.findById(widgetId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("findById returns the widget when it is present in the repository")
    void findById_shouldReturnsWidget_whenItIsPresentInTheRepository() {
        // given
        final var target = WidgetFixture.create(1);
        subject.saveAll(Set.of(target, WidgetFixture.create(2)));

        // when
        final Optional<Widget> actual = subject.findById(target.getId());

        // then
        assertThat(actual).isPresent().hasValue(target);
    }

    @Test
    @DisplayName("findAllOrderedByZIndex returns empty list when database is empty")
    void findAllOrderedByZIndex_shouldReturnsEmptyList_whenNoWidgetIsStored() {
        // when
        final List<Widget> actual = subject.findAllOrderedByZIndex();

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("findAllOrderedByZIndex returns list of widget order by zIndex")
    void findAllOrderedByZIndex_shouldReturnsZIndexOrderedWidgets() {
        // when
        final var widget1 = WidgetFixture.create(1);
        final var widget2 = WidgetFixture.create(2);
        subject.saveAll(Set.of(widget1, widget2));

        final List<Widget> actual = subject.findAllOrderedByZIndex();

        // then
        assertThat(actual).isNotEmpty().containsExactlyInAnyOrder(widget1, widget2);
    }

    @Test
    @DisplayName("findByZIndex returns empty when there is no widget with the given zIndex stored")
    void findByZIndex_shouldReturnsEmpty_whenNoWidgetWithGivenZIndexIsStored() {
        // given
        final int target = 0;
        subject.saveAll(Set.of(WidgetFixture.create(target + 1), WidgetFixture.create(target + 2)));

        // when
        final Optional<Widget> actual = subject.findByZIndex(target);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("findByZIndex returns the widget with the given zIndex when it is stored")
    void findByZIndex_shouldReturnsWidgetWithGivenZIndex_whenWidgetIsStored() {
        // given
        final int target = 0;
        final var widget = WidgetFixture.create(target);
        subject.saveAll(Set.of(widget, WidgetFixture.create(target + 1), WidgetFixture.create(target + 2)));

        // when
        final Optional<Widget> actual = subject.findByZIndex(target);

        // then
        assertThat(actual).isNotEmpty().hasValue(widget);
    }

    @Test
    @DisplayName("findNextZIndex returns the zero when there is not widget stored")
    void findNextZIndex_shouldReturnsZero_whenNoWidgetIsStored() {
        // when
        final int actual = subject.findNextZIndex();

        // then
        assertThat(actual).isEqualTo(0);
    }

    @Test
    @DisplayName("findNextZIndex returns the maximum zIndex plus one when there are widgets stored")
    void findNextZIndex_shouldReturnsNextMaxZIndex_whenWidgetsAreStored() {
        // given
        final var maxZIndex = 99;
        subject.saveAll(Set.of(WidgetFixture.create(1), WidgetFixture.create(maxZIndex)));

        // when
        final int actual = subject.findNextZIndex();

        // then
        assertThat(actual).isEqualTo(maxZIndex + 1);
    }

    @Test
    @DisplayName("saveAll saves a copy of the Widget updating its Last Modification Date")
    void saveAll_shouldSaveCopyOfWidgetWithLastModificationDateUpdated() {
        // given
        final var widget1 = WidgetFixture.create(1);
        final var widget2 = WidgetFixture.create(2);
        final var widget3 = WidgetFixture.create(3);

        // when
        final List<Widget> actual = subject.saveAll(Set.of(widget1, widget3, widget2));

        // then
        assertSavedWidget(actual.get(0), widget1);
        assertSavedWidget(actual.get(1), widget2);
        assertSavedWidget(actual.get(2), widget3);
    }

    private void assertSavedWidget(final Widget actual, final Widget expected) {
        final SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.getId()).isEqualTo(expected.getId());
        softAssertions.assertThat(actual.getCoordinateX()).isEqualTo(expected.getCoordinateX());
        softAssertions.assertThat(actual.getCoordinateY()).isEqualTo(expected.getCoordinateY());
        softAssertions.assertThat(actual.getZIndex()).isEqualTo(expected.getZIndex());
        softAssertions.assertThat(actual.getWidth()).isEqualTo(expected.getWidth());
        softAssertions.assertThat(actual.getHeight()).isEqualTo(expected.getHeight());
        softAssertions.assertThat(actual.getLastModificationDate()).isEqualTo(expected.getLastModificationDate());
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("saveAll updates existing widget and add new ones")
    void saveAll_shouldUpdateExistentWidget_whenSameIdStored() {
        // given
        final var widget1 = WidgetFixture.create(1);
        final var widget2 = WidgetFixture.create(2);
        subject.saveAll(Set.of(widget1, widget2));

        final var widget3 = WidgetFixture.create(3);

        final var widget = widget2.toBuilder().setZIndex(4).build();

        // when
        final List<Widget> actual = subject.saveAll(Set.of(widget, widget3));

        // then
        assertSavedWidget(actual.get(0), widget3);
        assertSavedWidget(actual.get(1), widget);
    }

    @Test
    @DisplayName("delete should remove match id from storage")
    void delete_shouldDeleteWidget_whenIdMatches() {
        // given
        final var widget1 = WidgetFixture.create(1);
        final var widget2 = WidgetFixture.create(2);
        subject.saveAll(Set.of(widget1, widget2));

        final UUID widget1Id = widget1.getId();

        // when
        subject.delete(widget1Id);

        // then
        assertThat(subject.findById(widget1Id)).isEmpty();
        assertSavedWidget(subject.findById(widget2.getId()).orElseThrow(), widget2);
    }

    @Test
    @DisplayName("deleteAll clean all the widget in the storage")
    void deleteAll_cleanAllTheWidgetInStorage() {
        // given
        final var widget1 = WidgetFixture.create(1);
        final var widget2 = WidgetFixture.create(2);
        subject.saveAll(Set.of(widget1, widget2));

        // when
        subject.deleteAll();

        // then
        assertThat(subject.findById(widget1.getId())).isEmpty();
        assertThat(subject.findById(widget2.getId())).isEmpty();
    }

}