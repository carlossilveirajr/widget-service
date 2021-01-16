package com.miro.widget.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.miro.widget.controller.dto.CreateWidgetDTO;
import com.miro.widget.controller.dto.UpdateWidgetDTO;
import com.miro.widget.controller.dto.WidgetDTO;
import com.miro.widget.model.Widget;
import com.miro.widget.service.WidgetService;

@RestController
@RequestMapping(path = WidgetController.PATH)
public class WidgetController {

    public static final String PATH = "/api/widgets";

    private final WidgetService widgetService;

    public WidgetController(final WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WidgetDTO createWidget(@RequestBody @Validated final CreateWidgetDTO widgetDTO) {
        final Widget widget = widgetService.createWidget(
                widgetDTO.getCoordinateX(),
                widgetDTO.getCoordinateY(),
                widgetDTO.getzIndex(),
                widgetDTO.getWidth(),
                widgetDTO.getHeight()
        );
        return WidgetDTO.from(widget);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public WidgetDTO updateWidget(@RequestBody @Validated final UpdateWidgetDTO widgetDTO) {
        final Widget widget = widgetService.updateWidget(
                widgetDTO.getId(),
                widgetDTO.getCoordinateX(),
                widgetDTO.getCoordinateY(),
                widgetDTO.getzIndex(),
                widgetDTO.getWidth(),
                widgetDTO.getHeight()
        );
        return WidgetDTO.from(widget);
    }

    @GetMapping(path = "/{widgetId}")
    public ResponseEntity<WidgetDTO> getWidget(@PathVariable("widgetId") final UUID widgetId) {
        return widgetService.findById(widgetId)
                .map(WidgetDTO::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WidgetDTO> getAllWidgets() {
        return widgetService.findAllOrderByZIndex().stream()
                .map(WidgetDTO::from)
                .collect(Collectors.toUnmodifiableList());
    }

    @DeleteMapping(path = "/{widgetId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteWidgetById(@PathVariable("widgetId") final UUID widgetId) {
        widgetService.deleteWidget(widgetId);
    }

}
