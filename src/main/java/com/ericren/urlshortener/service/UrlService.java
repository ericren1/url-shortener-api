package com.ericren.urlshortener.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.ericren.urlshortener.dto.CreateUrlResponse;
import com.ericren.urlshortener.entity.Url;
import com.ericren.urlshortener.exception.InvalidUrlException;
import com.ericren.urlshortener.exception.UrlNotFoundException;
import com.ericren.urlshortener.repository.UrlRepository;
import com.ericren.urlshortener.util.ShortCodeGenerator;
import com.ericren.urlshortener.util.UrlValidator;

@Service
public class UrlService {

    private static final int MAX_GENERATION_ATTEMPTS = 5;

    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final UrlValidator urlValidator;
    private final UrlCacheService urlCacheService;

    public UrlService(
            UrlRepository urlRepository,
            ShortCodeGenerator shortCodeGenerator,
            UrlValidator urlValidator,
            UrlCacheService urlCacheService
    ) {
        this.urlRepository = urlRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.urlValidator = urlValidator;
        this.urlCacheService = urlCacheService;
    }

    public CreateUrlResponse createShortUrl(String originalUrl) {

        if (!urlValidator.isValid(originalUrl)) {
            throw new InvalidUrlException("URL must use http or https and contain a valid host");
        }

        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {

            String shortCode = shortCodeGenerator.generate();

            try {
                Url saved = urlRepository.saveAndFlush(
                        new Url(
                                shortCode,
                                originalUrl,
                                Instant.now()
                        )
                );

                return new CreateUrlResponse(
                        saved.getShortCode(),
                        "http://localhost:8080/" + saved.getShortCode(),
                        saved.getOriginalUrl(),
                        saved.getCreatedAt()
                );

            } catch (DataIntegrityViolationException ex) {
                // Short-code collision. Retry with another generated code.
            }
        }

        throw new IllegalStateException(
                "Unable to generate a unique short code after multiple attempts"
        );
    }

    public String resolveOriginalUrl(String shortCode) {

        Optional<String> cachedUrl
                = urlCacheService.getOriginalUrl(shortCode);

        if (cachedUrl.isPresent()) {
            return cachedUrl.get();
        }

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        urlCacheService.cacheUrl(
                shortCode,
                url.getOriginalUrl()
        );

        return url.getOriginalUrl();
    }
}
