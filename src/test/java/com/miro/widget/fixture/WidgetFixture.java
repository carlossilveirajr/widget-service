package com.miro.widget.fixture;

import com.miro.widget.model.Widget;

public class WidgetFixture {

    private WidgetFixture() { }

    public static Widget create() {
        return create(1);
    }

    public static Widget create(final int zIndex) {
        return new Widget(1, 2, zIndex, 4, 5);
    }

}
