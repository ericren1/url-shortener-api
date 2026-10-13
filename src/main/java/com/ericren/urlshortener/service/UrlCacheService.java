package com.ericren.urlshortener.service;

import java.time.Duration;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UrlCacheService {

    private static final String KEY_PREFIX = "url:";
    private static final Duration TTL = Duration.ofHours(24);

    private final StringRedisTemplate redisTemplate;

    public UrlCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<String> getOriginalUrl(String shortCode) {
        try {
            String value = redisTemplate.opsForValue()
                    .get(KEY_PREFIX + shortCode);

            return Optional.ofNullable(value);

        } catch (DataAccessException ex) {
            return Optional.empty();
        }
    }

    public void cacheUrl(String shortCode, String originalUrl) {
        try {
            redisTemplate.opsForValue()
                    .set(
                            KEY_PREFIX + shortCode,
                            originalUrl,
                            TTL
                    );
        } catch (DataAccessException ignored) {
            // Cache failure should not break redirects.
        }
    }
}