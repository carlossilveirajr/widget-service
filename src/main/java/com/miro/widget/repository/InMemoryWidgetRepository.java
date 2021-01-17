package com.miro.widget.repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.miro.widget.model.Widget;
import com.miro.widget.util.Page;

/**
 * InMemoryWidgetRepository stores the widget in a memory.
 *
 * Two collections are used: widgetById is the main one and the first to be updated,
 *  the search operations that use only this collection can be done parallel.
 *  widgetIdByZIndex is the auxiliary collection that works as an index, it is the
 *  second to be updated, that way, it is necessary to synchronize all the operations
 *  that use that collection.
 */

@Component
class InMemoryWidgetRepository implements WidgetRepository {

    private static final int INITIAL_Z_INDEX_VALUE = 0;

    private final Map<UUID, Widget> widgetById;
    private final Map<Integer, UUID> widgetIdByZIndex;
    private final AtomicInteger nextZIndex;

    public InMemoryWidgetRepository() {
        widgetById = new ConcurrentHashMap<>();
        widgetIdByZIndex = new ConcurrentHashMap<>();
        nextZIndex = new AtomicInteger(INITIAL_Z_INDEX_VALUE);
    }

    @Override
    public Optional<Widget> findById(final UUID id) {
        return Optional.ofNullable(widgetById.get(id));
    }

    @Override
    public List<Widget> findAllOrderedByZIndex() {
        return widgetById.values().stream()
                .sorted(Comparator.comparingInt(Widget::getZIndex))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Widget> findByZIndex(final int zIndex) {
        synchronized (widgetIdByZIndex) {
            return Optional.ofNullable(widgetIdByZIndex.get(zIndex))
                    .map(widgetById::get);
        }
    }

    @Override
    public int findNextZIndex() {
        synchronized (widgetIdByZIndex) {
            return nextZIndex.getAndIncrement();
        }
    }

    @Override
    public List<Widget> saveAll(final Collection<Widget> widgets) {
        final Map<UUID, Widget> widgetById = widgets.stream()
                .collect(Collectors.toMap(Widget::getId, Function.identity()));
        final Map<Integer, UUID> idByZIndex = widgets.stream()
                .collect(Collectors.toMap(Widget::getZIndex, Widget::getId));

        final Set<UUID> keysToUpdate = widgetById.keySet();
        final Set<Integer> toRemove = widgetIdByZIndex.entrySet().stream()
                .filter(e -> keysToUpdate.contains(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());

        final int maxInsertedZIndex = idByZIndex.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);

        synchronized (widgetIdByZIndex) {
            this.widgetById.putAll(widgetById);

            nextZIndex.set(Math.max(nextZIndex.get(), maxInsertedZIndex + 1));

            toRemove.forEach(widgetIdByZIndex::remove);
            widgetIdByZIndex.putAll(idByZIndex);
        }

        return widgetById.values().stream()
                .sorted(Comparator.comparingInt(Widget::getZIndex))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(final UUID id) {
        synchronized (widgetIdByZIndex) {
            Optional.ofNullable(widgetById.remove(id))
                    .map(Widget::getZIndex)
                    .ifPresent(widgetIdByZIndex::remove);
        }
    }

    @Override
    public void deleteAll() {
        widgetById.clear();
        widgetIdByZIndex.clear();
    }

    @Override
    public List<Widget> findAllOrderedByZIndex(final Page page) {
        final int start = page.getPage() * page.getSize();
        final int candidateEnd = start + page.getSize();
        final int end = Math.min(widgetById.size(), candidateEnd);

        if (end - start <= 0) {
            return List.of();
        }

        return findAllOrderedByZIndex().subList(start, end);
    }
}
