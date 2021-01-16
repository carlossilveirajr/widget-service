package com.miro.widget.controller.dto;

import java.util.Objects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateWidgetDTO {

    @NotNull
    private final Integer coordinateX;

    @NotNull
    private final Integer coordinateY;

    private final Integer zIndex;

    @NotNull
    @Min(1)
    private final Integer width;

    @NotNull
    @Min(1)
    private final Integer height;

    public CreateWidgetDTO(
            final Integer coordinateX,
            final Integer coordinateY,
            final Integer zIndex,
            final Integer width,
            final Integer height
    ) {
        this.coordinateX = Objects.requireNonNull(coordinateX);
        this.coordinateY = Objects.requireNonNull(coordinateY);
        this.zIndex = zIndex;
        this.width = Objects.requireNonNull(width);
        this.height = Objects.requireNonNull(height);
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

}
