package com.ericren.urlshortener.service;

import com.ericren.urlshortener.dto.CreateUrlResponse;
import com.ericren.urlshortener.entity.Url;
import com.ericren.urlshortener.exception.InvalidUrlException;
import com.ericren.urlshortener.repository.ClickEventRepository;
import com.ericren.urlshortener.repository.UrlRepository;
import com.ericren.urlshortener.util.ShortCodeGenerator;
import com.ericren.urlshortener.util.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UrlServiceTest {

    private UrlRepository urlRepository;
    private ShortCodeGenerator shortCodeGenerator;
    private UrlValidator urlValidator;
    private UrlCacheService urlCacheService;
    private ClickEventRepository clickEventRepository;

    private UrlService urlService;

    @BeforeEach
    void setUp() {
        urlRepository = Mockito.mock(UrlRepository.class);
        shortCodeGenerator = Mockito.mock(ShortCodeGenerator.class);
        urlValidator = Mockito.mock(UrlValidator.class);
        urlCacheService = Mockito.mock(UrlCacheService.class);
        clickEventRepository = Mockito.mock(ClickEventRepository.class);

        urlService = new UrlService(
                urlRepository,
                shortCodeGenerator,
                urlValidator,
                urlCacheService,
                clickEventRepository
        );
    }

    @Test
    void createShortUrl_shouldCreateUrl_whenValid() {
        String originalUrl = "https://example.com";
        String shortCode = "Ab12Cd34";
        Instant now = Instant.now();

        when(urlValidator.isValid(originalUrl)).thenReturn(true);
        when(shortCodeGenerator.generate()).thenReturn(shortCode);

        when(urlRepository.saveAndFlush(any(Url.class)))
                .thenReturn(new Url(shortCode, originalUrl, now));

        CreateUrlResponse response =
                urlService.createShortUrl(originalUrl);

        assertEquals(shortCode, response.shortCode());
        assertEquals(originalUrl, response.originalUrl());
        assertEquals(
                "http://localhost:8080/" + shortCode,
                response.shortUrl()
        );
    }

    @Test
    void createShortUrl_shouldThrow_whenUrlInvalid() {
        String invalidUrl = "hello";

        when(urlValidator.isValid(invalidUrl)).thenReturn(false);

        assertThrows(
                InvalidUrlException.class,
                () -> urlService.createShortUrl(invalidUrl)
        );
    }
}