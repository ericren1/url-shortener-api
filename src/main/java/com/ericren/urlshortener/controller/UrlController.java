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
import com.ericren.urlshortener.exception.RateLimitExceededException;
import com.ericren.urlshortener.service.RateLimitService;
import com.ericren.urlshortener.service.UrlService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final UrlService urlService;
    private final RateLimitService rateLimitService;

    public UrlController(
            UrlService urlService,
            RateLimitService rateLimitService
    ) {
        this.urlService = urlService;
        this.rateLimitService = rateLimitService;
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
            @Valid @RequestBody CreateUrlRequest request,
            HttpServletRequest httpRequest
    ) {

        String clientIp = httpRequest.getRemoteAddr();

        if (!rateLimitService.isAllowed(clientIp)) {
            throw new RateLimitExceededException();
        }

        return urlService.createShortUrl(request.url());
    }
}
