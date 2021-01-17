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
            final int widgetZIndex = getWidgetZIndex(zIndex);
            final var widget = new Widget(coordinateX, coordinateY, widgetZIndex, width, height);

            final Set<Widget> widgets = shiftWidgets(widgetZIndex);
            widgets.add(widget);

            repository.saveAll(widgets);

            return widget;
        });
    }

    private synchronized <T> T doInTransaction(final Supplier<T> function) {
        return function.get();
    }

    private Integer getWidgetZIndex(final Integer zIndex) {
        return Optional.ofNullable(zIndex).orElseGet(repository::findNextZIndex);
    }

    private Set<Widget> shiftWidgets(final int widgetZIndex) {
        final Set<Widget> shiftedWidget = new HashSet<>();

        // using AtomicInteger because it is incremented in the lambda (not because of synchronization)
        final AtomicInteger index = new AtomicInteger(widgetZIndex);
        Optional<Widget> widget;

        do {
            widget = repository.findByZIndex(index.get());
            widget.ifPresent(w -> {
                w.setZIndex(index.incrementAndGet());
                shiftedWidget.add(w);
            });
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
        Optional.ofNullable(coordinateX).ifPresent(widget::setCoordinateX);
        Optional.ofNullable(coordinateY).ifPresent(widget::setCoordinateY);
        Optional.ofNullable(width).ifPresent(widget::setWidth);
        Optional.ofNullable(height).ifPresent(widget::setHeight);

        return doInTransaction(() -> {
            final Set<Widget> widgetsToUpdate = new HashSet<>();

            if (Objects.isNull(zIndex)) {
                widget.setZIndex(repository.findNextZIndex());
            } else if (zIndex != widget.getZIndex()) {
                widget.setZIndex(zIndex);

                final Set<Widget> shiftedWidgets = shiftWidgets(zIndex);
                widgetsToUpdate.addAll(shiftedWidgets);
            }

            widgetsToUpdate.add(widget);
            repository.saveAll(widgetsToUpdate);

            return widget;
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
