package com.ericren.urlshortener.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ericren.urlshortener.dto.CreateUrlRequest;
import com.ericren.urlshortener.dto.CreateUrlResponse;
import com.ericren.urlshortener.dto.UrlAnalyticsResponse;
import com.ericren.urlshortener.service.UrlService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        
        this.urlService = urlService;
    }

    @GetMapping("/{shortCode}/analytics")
public UrlAnalyticsResponse getAnalytics(
        @PathVariable String shortCode
) {
    return urlService.getAnalytics(shortCode);
}
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUrlResponse createUrl(
            @Valid @RequestBody CreateUrlRequest request
    ) {
        return urlService.createShortUrl(request.url());
    }
}