package com.miro.widget.controller.dto;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.miro.widget.model.Widget;

public class WidgetDTO {

    @NotNull
    private final UUID id;

    @NotNull
    private final ZonedDateTime lastModificationDate;

    @NotNull
    private final Integer coordinateX;

    @NotNull
    private final Integer coordinateY;

    @NotNull
    private final Integer zIndex;

    @NotNull
    private final Integer width;

    @NotNull
    private final Integer height;

    public WidgetDTO(
            final UUID id,
            final ZonedDateTime lastModificationDate,
            final Integer coordinateX,
            final Integer coordinateY,
            final Integer zIndex,
            final Integer width,
            final Integer height
    ) {
        this.id = Objects.requireNonNull(id);
        this.lastModificationDate = Objects.requireNonNull(lastModificationDate);
        this.coordinateX = Objects.requireNonNull(coordinateX);
        this.coordinateY = Objects.requireNonNull(coordinateY);
        this.zIndex = Objects.requireNonNull(zIndex);
        this.width = Objects.requireNonNull(width);
        this.height = Objects.requireNonNull(height);
    }

    public UUID getId() {
        return id;
    }

    public ZonedDateTime getLastModificationDate() {
        return lastModificationDate;
    }

    public Integer getCoordinateX() {
        return coordinateX;
    }

    public Integer getCoordinateY() {
        return coordinateY;
    }

    public Integer getzIndex() {
        return zIndex;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }


    public static WidgetDTO from(final Widget widget) {
        return new WidgetDTO(
                widget.getId(),
                widget.getLastModificationDate(),
                widget.getCoordinateX(),
                widget.getCoordinateY(),
                widget.getZIndex(),
                widget.getWidth(),
                widget.getHeight()
        );
    }

}
