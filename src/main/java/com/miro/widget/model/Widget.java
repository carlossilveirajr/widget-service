package com.miro.widget.model;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

public class Widget {

    private final UUID id;

    private ZonedDateTime lastModificationDate;

    private int coordinateX;

    private int coordinateY;

    private int zIndex;

    private int width;

    private int height;

    public Widget(final Widget widget) {
        this(
                widget.getId(),
                widget.getLastModificationDate(),
                widget.getCoordinateX(),
                widget.getCoordinateY(),
                widget.getZIndex(),
                widget.getWidth(),
                widget.getHeight()
        );
    }

    public Widget(
            final int coordinateX,
            final int coordinateY,
            final int zIndex,
            final int width,
            final int height
    ) {
        this(UUID.randomUUID(), ZonedDateTime.now(), coordinateX, coordinateY, zIndex, width, height);
    }

    public Widget(
            final UUID id,
            final ZonedDateTime lastModificationDate,
            final int coordinateX,
            final int coordinateY,
            final int zIndex,
            final int width,
            final int height
    ) {
        this.id = Objects.requireNonNull(id);
        this.lastModificationDate = Objects.requireNonNull(lastModificationDate);

        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.zIndex = zIndex;
        setWidth(width);
        setHeight(height);
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

    public void setCoordinateX(final int coordinateX) {
        this.coordinateX = coordinateX;
    }

    public void setCoordinateY(final int coordinateY) {
        this.coordinateY = coordinateY;
    }

    public void setZIndex(final int zIndex) {
        this.zIndex = zIndex;
    }

    public void setWidth(final int width) {
        checkArgument(width > 0, "Width must be positive");
        this.width = width;
    }

    public void setHeight(final int height) {
        checkArgument(height > 0, "Height must be positive");
        this.height = height;
    }

    public void updateLastModificationDate() {
        this.lastModificationDate = ZonedDateTime.now();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Widget widget = (Widget) o;
        return coordinateX == widget.coordinateX &&
                coordinateY == widget.coordinateY &&
                zIndex == widget.zIndex &&
                width == widget.width &&
                height == widget.height &&
                id.equals(widget.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, coordinateX, coordinateY, zIndex, width, height);
    }

}
