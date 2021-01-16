package com.miro.widget.controller.dto;

import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class UpdateWidgetDTO {

    @NotNull
    private final UUID id;

    private final Integer coordinateX;

    private final Integer coordinateY;

    private final Integer zIndex;

    @Min(1)
    private final Integer width;

    @Min(1)
    private final Integer height;

    public UpdateWidgetDTO(
            final UUID id,
            final Integer coordinateX,
            final Integer coordinateY,
            final Integer zIndex,
            final Integer width,
            final Integer height
    ) {
        this.id = Objects.requireNonNull(id);
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.zIndex = zIndex;
        this.width = width;
        this.height = height;
    }

    public UUID getId() {
        return id;
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
