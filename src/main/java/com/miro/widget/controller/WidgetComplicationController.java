package com.miro.widget.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.miro.widget.controller.dto.WidgetDTO;
import com.miro.widget.service.WidgetService;
import com.miro.widget.util.Page;

@RestController
@RequestMapping(path = WidgetComplicationController.PATH)
public class WidgetComplicationController {

    public static final String PATH = "/api/v2/widgets";

    private final WidgetService widgetService;

    public WidgetComplicationController(final WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    // complication 1
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WidgetDTO> getPagedWidget(
            @RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "size", defaultValue = "10") final int size
    ) {
        return widgetService.findAllOrderByZIndex(Page.from(page, size)).stream()
                .map(WidgetDTO::from)
                .collect(Collectors.toUnmodifiableList());
    }

}
