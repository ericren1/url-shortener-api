package com.ericren.urlshortener.dto;

import java.time.Instant;

public record UrlAnalyticsResponse(
        String shortCode,
        String originalUrl,
        long totalClicks,
        Instant createdAt,
        Instant lastClickedAt
) {
}