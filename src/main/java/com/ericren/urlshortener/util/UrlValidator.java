package com.ericren.urlshortener.util;

import java.net.URI;

import org.springframework.stereotype.Component;

@Component
public class UrlValidator {

    public boolean isValid(String value) {
        try {
            URI uri = URI.create(value);

            String scheme = uri.getScheme();

            return scheme != null
                    && (scheme.equalsIgnoreCase("http")
                    || scheme.equalsIgnoreCase("https"))
                    && uri.getHost() != null;

        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}