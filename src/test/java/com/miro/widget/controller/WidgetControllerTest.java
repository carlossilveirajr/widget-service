package com.miro.widget.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.miro.widget.controller.dto.CreateWidgetDTO;
import com.miro.widget.controller.dto.UpdateWidgetDTO;
import com.miro.widget.fixture.WidgetFixture;
import com.miro.widget.service.WidgetService;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(controllers = {WidgetController.class})
@DisplayName("Widget Controller Test")
class WidgetControllerTest {

    @MockBean
    private WidgetService widgetServiceMock;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.reset();
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());
        RestAssuredMockMvc.basePath = WidgetController.PATH;
    }

    @Test
    @DisplayName("POST should create a widget and return ok")
    void createWidget_shouldReturnsOk_whenNewWidgetIsCreated() {
        // given
        final var widget = WidgetFixture.create();
        final var id = widget.getId();
        final var coordinateX = widget.getCoordinateX();
        final var coordinateY = widget.getCoordinateY();
        final var zIndex = widget.getZIndex();
        final var width = widget.getWidth();
        final var height = widget.getHeight();

        when(widgetServiceMock.createWidget(coordinateX, coordinateY, zIndex, width, height))
                .thenReturn(widget);

        final var create = new CreateWidgetDTO(coordinateX, coordinateY, zIndex, width, height);

        // when - then
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(create)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", equalTo(id.toString()))
                .body("coordinateX", equalTo(coordinateX))
                .body("coordinateY", equalTo(coordinateY))
                .body("zIndex", equalTo(zIndex))
                .body("width", equalTo(width))
                .body("height", equalTo(height));
    }

    @Test
    @DisplayName("PUT returns Bad Request when the updated widget is not found")
    void updateWidget_shouldReturnBadRequest_whenWidgetNotFound() {
        // given
        final var widget = WidgetFixture.create();
        final var id = widget.getId();
        final var coordinateX = widget.getCoordinateX();
        final var coordinateY = widget.getCoordinateY();
        final var zIndex = widget.getZIndex();
        final var width = widget.getWidth();
        final var height = widget.getHeight();

        when(widgetServiceMock.updateWidget(id, coordinateX, coordinateY, zIndex, width, height))
                .thenThrow(new WidgetService.WidgetNotFoundException(id));

        final var update = new UpdateWidgetDTO(id, coordinateX, coordinateY, zIndex, width, height);

        // when - then
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(update)
                .when()
                .put()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("PUT updates widget description based on its id")
    void updateWidget_shouldReturnsUpdatedWidget_whenUpdateIsCall() {
        // given
        final var widget = WidgetFixture.create();
        final var id = widget.getId();
        final var coordinateX = widget.getCoordinateX();
        final var coordinateY = widget.getCoordinateY();
        final var zIndex = widget.getZIndex();
        final var width = widget.getWidth();
        final var height = widget.getHeight();

        when(widgetServiceMock.updateWidget(id, coordinateX, coordinateY, zIndex, width, height))
                .thenReturn(widget);

        final var update = new UpdateWidgetDTO(id, coordinateX, coordinateY, zIndex, width, height);

        // when - then
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(update)
                .when()
                .put()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id.toString()))
                .body("coordinateX", equalTo(coordinateX))
                .body("coordinateY", equalTo(coordinateY))
                .body("zIndex", equalTo(zIndex))
                .body("width", equalTo(width))
                .body("height", equalTo(height));
    }

    @Test
    @DisplayName("GET by id returns the widget when it is found")
    void getWidget_shouldReturnWidget_whenWidgetItIsFound() {
        // given
        final var widget = WidgetFixture.create();
        final var widgetId = widget.getId();
        when(widgetServiceMock.findById(widgetId)).thenReturn(Optional.of(widget));

        // when - then
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/{widgetId}", widgetId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(widgetId.toString()))
                .body("coordinateX", equalTo(widget.getCoordinateX()))
                .body("coordinateY", equalTo(widget.getCoordinateY()))
                .body("zIndex", equalTo(widget.getZIndex()))
                .body("width", equalTo(widget.getWidth()))
                .body("height", equalTo(widget.getHeight()));
    }

    @Test
    @DisplayName("GET by id returns no content when widget is not found")
    void getWidget_shouldReturnNoContent_whenWidgetIsNotFound() {
        // given
        final var widgetId = UUID.randomUUID();
        when(widgetServiceMock.findById(widgetId)).thenReturn(Optional.empty());

        // when - then
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/{widgetId}", widgetId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("GET returns a list of all widget order by zIndex")
    void getAllWidgets_shouldGetListOfWidgetByZIndex() {
        // given
        final var widget1 = WidgetFixture.create(1);
        final var widget2 = WidgetFixture.create(2);
        when(widgetServiceMock.findAllOrderByZIndex()).thenReturn(List.of(widget1, widget2));

        // when - then
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", contains(widget1.getId().toString(), widget2.getId().toString()));
    }

    @Test
    @DisplayName("DELETE should remove widget by id and returns OK")
    void deleteWidgetById_shouldCallWidgetServiceToDelete() {
        // given
        final var widgetId = UUID.randomUUID();

        // when - then
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/{widgetId}", widgetId)
                .then()
                .statusCode(HttpStatus.OK.value());

        verify(widgetServiceMock).deleteWidget(eq(widgetId));
    }
}