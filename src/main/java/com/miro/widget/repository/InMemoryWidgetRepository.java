package com.miro.widget.repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.miro.widget.model.Widget;
import com.miro.widget.util.Page;

@Component
class InMemoryWidgetRepository implements WidgetRepository {

    private final Map<UUID, Widget> widgetDatabase;
    private final Map<Integer, UUID> zIndexDatabase;

    public InMemoryWidgetRepository() {
        widgetDatabase = new ConcurrentHashMap<>();
        zIndexDatabase = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<Widget> findById(final UUID id) {
        return Optional.ofNullable(widgetDatabase.get(id))
                .map(Widget::new);
    }

    @Override
    public List<Widget> findAllOrderedByZIndex() {
        return widgetDatabase.values().stream()
                .sorted(Comparator.comparingInt(Widget::getZIndex))
                .map(Widget::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Widget> findByZIndex(final int zIndex) {
        synchronized (zIndexDatabase) {
            return Optional.ofNullable(zIndexDatabase.get(zIndex))
                    .map(widgetDatabase::get)
                    .map(Widget::new);
        }
    }

    @Override
    public int findNextZIndex() {
        synchronized (zIndexDatabase) {
            return zIndexDatabase.keySet().stream()
                    .max(Comparator.naturalOrder())
                    .map(i -> i + 1)
                    .orElse(Integer.MIN_VALUE);
        }
    }

    @Override
    public List<Widget> saveAll(final Collection<Widget> widgets) {
        final Map<UUID, Widget> widgetsById = widgets.stream()
                .map(Widget::new)
                .peek(Widget::updateLastModificationDate)
                .collect(Collectors.toMap(Widget::getId, Function.identity()));
        final Map<Integer, UUID> idByZIndex = widgets.stream()
                .collect(Collectors.toMap(Widget::getZIndex, Widget::getId));

        final Set<UUID> keysToUpdate = widgetsById.keySet();
        final Set<Integer> toRemove = zIndexDatabase.entrySet().stream()
                .filter(e -> keysToUpdate.contains(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());

        synchronized (zIndexDatabase) {
            widgetDatabase.putAll(widgetsById);
            toRemove.forEach(zIndexDatabase::remove);
            zIndexDatabase.putAll(idByZIndex);
        }

        return widgetsById.values().stream()
                .map(Widget::new)
                .sorted(Comparator.comparingInt(Widget::getZIndex))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(final UUID id) {
        synchronized (zIndexDatabase) {
            Optional.ofNullable(widgetDatabase.remove(id))
                    .map(Widget::getZIndex)
                    .ifPresent(zIndexDatabase::remove);
        }
    }

    @Override
    public void deleteAll() {
        widgetDatabase.clear();
        zIndexDatabase.clear();
    }

    @Override
    public List<Widget> findAllOrderedByZIndex(final Page page) {
        final int start = page.getPage() * page.getSize();
        final int candidateEnd = start + page.getSize();
        final int end = Math.max(widgetDatabase.size(), candidateEnd);

        if (end - start <= 0) {
            return List.of();
        }

        return findAllOrderedByZIndex().subList(start, end);
    }
}
