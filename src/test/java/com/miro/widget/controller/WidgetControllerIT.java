package com.miro.widget.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.miro.widget.controller.dto.CreateWidgetDTO;
import com.miro.widget.controller.dto.UpdateWidgetDTO;
import com.miro.widget.fixture.WidgetFixture;
import com.miro.widget.model.Widget;
import com.miro.widget.repository.InMemoryWidgetRepository;
import com.miro.widget.repository.WidgetRepository;
import com.miro.widget.service.WidgetService;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(controllers = {WidgetController.class})
@Import({WidgetService.class, InMemoryWidgetRepository.class})
@DisplayName("Widget Controller Integrated Test")
public class WidgetControllerIT {

    @Autowired
    private WidgetRepository repository;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.reset();
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());
        RestAssuredMockMvc.basePath = WidgetController.PATH;

        repository.deleteAll();
    }

    @Test
    @DisplayName("Should be possible to read widget after its creation")
    void happyPath_shouldBePossibleToReadWidget_AfterCreation() {
        // given
        final Widget widget = WidgetFixture.create(1);

        final Widget widget1 = createWidget(widget);
        final Widget widget2 = createWidget(widget);

        // when - then
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/{widgetId}", widget1.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(widget1.getId().toString()))
                .body("zIndex", equalTo(2));

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/{widgetId}", widget2.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(widget2.getId().toString()))
                .body("zIndex", equalTo(1));
    }

    private Widget createWidget(final Widget widget) {
        final var coordinateX = widget.getCoordinateX();
        final var coordinateY = widget.getCoordinateY();
        final var zIndex = widget.getZIndex();
        final var width = widget.getWidth();
        final var height = widget.getHeight();

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(new CreateWidgetDTO(coordinateX, coordinateY, zIndex, width, height))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("coordinateX", equalTo(coordinateX))
                .body("coordinateY", equalTo(coordinateY))
                .body("zIndex", equalTo(zIndex))
                .body("width", equalTo(width))
                .body("height", equalTo(height));

        return repository.findByZIndex(zIndex).orElseThrow();
    }

    @Test
    @DisplayName("After a Widget is delete is shouldn't appears in any get")
    void happyPath_afterDeletionTheWidgetShouldNotBeRetrieved() {
        // given
        final Widget widget = WidgetFixture.create(1);

        final Widget widget1 = createWidget(widget);
        final Widget widget2 = createWidget(widget);
        final Widget widget3 = createWidget(widget);

        // when - then
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/{widgetId}", widget2.getId())
                .then()
                .statusCode(HttpStatus.OK.value());

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/{widgetId}", widget2.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", contains(widget3.getId().toString(), widget1.getId().toString()));
    }

    @Test
    @DisplayName("After a Widget is update the get must get the last version")
    void happyPath_updateMustChangeTheWidgetDescription() {
        // given
        final int zIndex = 1;
        final Widget widget = createWidget(WidgetFixture.create(zIndex));

        // when - then
        final var update = new UpdateWidgetDTO(widget.getId(), widget.getCoordinateX() + 1, null, null, widget.getWidth(), widget.getHeight() + 1);

        // when - then
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(update)
                .when()
                .put()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(widget.getId().toString()))
                .body("coordinateX", equalTo(widget.getCoordinateX() + 1))
                .body("coordinateY", equalTo(widget.getCoordinateY()))
                .body("zIndex", equalTo(zIndex + 1))
                .body("width", equalTo(widget.getWidth()))
                .body("height", equalTo(widget.getHeight() + 1));

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/{widgetId}", widget.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(widget.getId().toString()))
                .body("coordinateX", equalTo(widget.getCoordinateX() + 1))
                .body("coordinateY", equalTo(widget.getCoordinateY()))
                .body("zIndex", equalTo(zIndex + 1))
                .body("width", equalTo(widget.getWidth()))
                .body("height", equalTo(widget.getHeight() + 1));
    }

}
