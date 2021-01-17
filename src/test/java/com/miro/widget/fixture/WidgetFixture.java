package com.miro.widget.fixture;

import java.util.UUID;

import com.miro.widget.model.Widget;

public class WidgetFixture {

    private WidgetFixture() { }

    public static Widget create() {
        return create(1);
    }

    public static Widget create(final int zIndex) {
        return Widget.builder()
                .setId(UUID.randomUUID())
                .setCoordinateX(1)
                .setCoordinateY(2)
                .setZIndex(zIndex)
                .setWidth(4)
                .setHeight(5)
                .build();
    }

}
