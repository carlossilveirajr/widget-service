package com.miro.widget.controller;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.miro.widget.fixture.WidgetFixture;
import com.miro.widget.model.Widget;
import com.miro.widget.service.WidgetService;
import com.miro.widget.util.Page;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(controllers = {WidgetComplicationController.class})
@DisplayName("Widget Complication Controller Test")
class WidgetComplicationControllerTest {

    @MockBean
    private WidgetService widgetServiceMock;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.reset();
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());
        RestAssuredMockMvc.basePath = WidgetComplicationController.PATH;
    }

    @Test
    @DisplayName("GET uses the default page to bring the widget")
    void getPagedWidget_shouldReturnFoundWidget_whenNoPaginationIsSentUsingDefaultValues() {
        // given
        final Widget widget1 = WidgetFixture.create();
        final Widget widget2 = WidgetFixture.create();

        when(widgetServiceMock.findAllOrderByZIndex(Page.from(0, 10)))
                .thenReturn(List.of(widget1, widget2));

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
    @DisplayName("GET should use page set up to retrieve widget")
    void getPagedWidget_shouldUsePageSetUp_whenPassedAsParameters() {
        // given
        final Widget widget1 = WidgetFixture.create();
        final Widget widget2 = WidgetFixture.create();

        final int page = 1;
        final int size = 5;
        when(widgetServiceMock.findAllOrderByZIndex(Page.from(page, size)))
                .thenReturn(List.of(widget1, widget2));

        // when - then
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .params(Map.of("page", page, "size", size))
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", contains(widget1.getId().toString(), widget2.getId().toString()));
    }

    @Test
    @DisplayName("GET returns a BAD REQUEST when the page configuration is not valid")
    void getPagedWidget_shouldThrowsException_whenInvalidPageConfigurationIsSent() {
        // given
        final int page = -1;
        final int size = 5;

        // when - then
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .params(Map.of("page", page, "size", size))
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

}