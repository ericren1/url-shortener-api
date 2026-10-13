package com.ericren.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUrlRequest(
        @NotBlank(message = "URL must not be blank")
        String url
) {
}