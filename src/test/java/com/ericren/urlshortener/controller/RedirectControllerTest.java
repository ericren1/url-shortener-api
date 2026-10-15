package com.ericren.urlshortener.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ericren.urlshortener.exception.GlobalExceptionHandler;
import com.ericren.urlshortener.exception.UrlNotFoundException;
import com.ericren.urlshortener.service.UrlService;

@WebMvcTest({
        RedirectController.class,
        GlobalExceptionHandler.class
})
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @Test
    void redirect_shouldReturn302() throws Exception {
        when(urlService.resolveOriginalUrl("Ab12Cd34"))
                .thenReturn("https://example.com");

        mockMvc.perform(
                        get("/Ab12Cd34")
                )
                .andExpect(status().isFound())
                .andExpect(
                        header().string(
                                "Location",
                                "https://example.com"
                        )
                );
    }

    @Test
    void redirect_shouldReturn404_whenMissing() throws Exception {
        when(urlService.resolveOriginalUrl("Missing1"))
                .thenThrow(new UrlNotFoundException("Missing1"));

        mockMvc.perform(
                        get("/Missing1")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}