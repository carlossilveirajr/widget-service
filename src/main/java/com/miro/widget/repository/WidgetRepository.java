package com.miro.widget.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.miro.widget.model.Widget;

public interface WidgetRepository {

    Optional<Widget> findById(UUID id);

    List<Widget> findAllOrderedByZIndex();

    Optional<Widget> findByZIndex(int zIndex);

    int findNextZIndex();

    List<Widget> saveAll(Iterable<Widget> widgets);

    void delete(UUID id);

    void deleteAll();
}
