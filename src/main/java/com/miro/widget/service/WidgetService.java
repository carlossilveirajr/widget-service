package com.miro.widget.service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import com.miro.widget.model.Widget;
import com.miro.widget.repository.WidgetRepository;
import com.miro.widget.util.Page;

/**
 * Widget Service handles operations with Widgets such as creation, update, search, and deletion.
 *
 * In this service, the Widgets shift logic is implemented when Z-index collision is detected.
 *  That way, all the time the widgets are saved in the repository the correct information is sent
 *  avoiding the need for unicity checking in the data storage. Operations that demand shift
 *  (creation and update) are done in a serial way.
 */

@Service
public class WidgetService {

    private final WidgetRepository repository;

    public WidgetService(final WidgetRepository repository) {
        this.repository = repository;
    }

    public Widget createWidget(
            final int coordinateX,
            final int coordinateY,
            final Integer zIndex,
            final int width,
            final int height
    ) {
        return doInTransaction(() -> {
            final int widgetZIndex = Optional.ofNullable(zIndex)
                    .orElseGet(repository::findNextZIndex);
            final var widget = Widget.builder()
                    .setId(UUID.randomUUID())
                    .setCoordinateX(coordinateX)
                    .setCoordinateY(coordinateY)
                    .setZIndex(widgetZIndex)
                    .setWidth(width)
                    .setHeight(height)
                    .build();

            final Set<Widget> widgets = shiftWidgets(widgetZIndex);
            widgets.add(widget);

            repository.saveAll(widgets);

            return widget;
        });
    }

    private synchronized <T> T doInTransaction(final Supplier<T> function) {
        return function.get();
    }

    private Set<Widget> shiftWidgets(final int widgetZIndex) {
        final Set<Widget> shiftedWidget = new HashSet<>();

        // using AtomicInteger because it is incremented in the lambda (not because of synchronization)
        final AtomicInteger index = new AtomicInteger(widgetZIndex);
        Optional<Widget> widget;

        do {
            widget = repository.findByZIndex(index.get());
            widget.map(w -> w.toBuilder()
                    .setZIndex(index.incrementAndGet())
                    .build()
            ).ifPresent(shiftedWidget::add);
        } while (widget.isPresent());

        return shiftedWidget;
    }

    public Widget updateWidget(
            final UUID widgetId,
            final Integer coordinateX,
            final Integer coordinateY,
            final Integer zIndex,
            final Integer width,
            final Integer height
    ) {
        final Widget widget = repository.findById(widgetId)
                .orElseThrow(() -> new WidgetNotFoundException(widgetId));

        final Widget.WidgetBuilder widgetBuilder = widget.toBuilder();
        Optional.ofNullable(coordinateX).ifPresent(widgetBuilder::setCoordinateX);
        Optional.ofNullable(coordinateY).ifPresent(widgetBuilder::setCoordinateY);
        Optional.ofNullable(width).ifPresent(widgetBuilder::setWidth);
        Optional.ofNullable(height).ifPresent(widgetBuilder::setHeight);

        return doInTransaction(() -> {
            final Set<Widget> widgetsToUpdate = new HashSet<>();

            if (Objects.isNull(zIndex)) {
                widgetBuilder.setZIndex(repository.findNextZIndex());
            } else if (zIndex != widget.getZIndex()) {
                widgetBuilder.setZIndex(zIndex);

                final Set<Widget> shiftedWidgets = shiftWidgets(zIndex);
                widgetsToUpdate.addAll(shiftedWidgets);
            }

            final Widget updatedWidget = widgetBuilder.build();

            widgetsToUpdate.add(updatedWidget);
            repository.saveAll(widgetsToUpdate);

            return updatedWidget;
        });
    }

    public void deleteWidget(final UUID widgetId) {
        doInTransaction(() -> {
            repository.delete(widgetId);
            return null;
        });
    }

    public Optional<Widget> findById(final UUID id) {
        return repository.findById(id);
    }

    public List<Widget> findAllOrderByZIndex() {
        return repository.findAllOrderedByZIndex();
    }

    // Complication 1
    public List<Widget> findAllOrderByZIndex(final Page page) {
        return repository.findAllOrderedByZIndex(page);
    }

    public static class WidgetNotFoundException extends RuntimeException {

        public WidgetNotFoundException(final UUID widgetId) {
            super(String.format("Widget %s not found", widgetId));
        }

    }

}
