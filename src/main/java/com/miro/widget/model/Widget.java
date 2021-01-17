package com.miro.widget.model;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * This object represents a Widget.
 *
 * This object is immutable to ensure thread-safe of writing and update Widget in
 *  the widget-service. In case you need to change any field a new instance should
 *  be created using the builder pattern.
 */

public final class Widget {

    private final UUID id;

    private final ZonedDateTime lastModificationDate;

    private final int coordinateX;

    private final int coordinateY;

    private final int zIndex;

    private final int width;

    private final int height;

    private Widget(
            final UUID id,
            final int coordinateX,
            final int coordinateY,
            final int zIndex,
            final int width,
            final int height
    ) {
        this.id = Objects.requireNonNull(id);
        this.lastModificationDate = ZonedDateTime.now();

        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.zIndex = zIndex;

        checkArgument(width > 0, "Width must be positive");
        checkArgument(height > 0, "Height must be positive");
        this.width = width;
        this.height = height;
    }

    public UUID getId() {
        return id;
    }

    public ZonedDateTime getLastModificationDate() {
        return lastModificationDate;
    }

    public int getCoordinateX() {
        return coordinateX;
    }

    public int getCoordinateY() {
        return coordinateY;
    }

    public int getZIndex() {
        return zIndex;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Widget widget = (Widget) o;
        return id.equals(widget.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public WidgetBuilder toBuilder() {
        return new WidgetBuilder()
                .setId(id)
                .setCoordinateX(coordinateX)
                .setCoordinateY(coordinateY)
                .setZIndex(zIndex)
                .setHeight(height)
                .setWidth(width);
    }

    public static WidgetBuilder builder() {
        return new WidgetBuilder();
    }

    public static class WidgetBuilder {
        private UUID id;
        private Integer coordinateX;
        private Integer coordinateY;
        private Integer zIndex;
        private Integer width;
        private Integer height;

        public WidgetBuilder setId(final UUID id) {
            this.id = id;
            return this;
        }

        public WidgetBuilder setCoordinateX(final int coordinateX) {
            this.coordinateX = coordinateX;
            return this;
        }

        public WidgetBuilder setCoordinateY(final int coordinateY) {
            this.coordinateY = coordinateY;
            return this;
        }

        public WidgetBuilder setZIndex(final int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        public WidgetBuilder setWidth(final int width) {
            this.width = width;
            return this;
        }

        public WidgetBuilder setHeight(final int height) {
            this.height = height;
            return this;
        }

        public Widget build() {
            return new Widget(id, coordinateX, coordinateY, zIndex, width, height);
        }

    }

}
