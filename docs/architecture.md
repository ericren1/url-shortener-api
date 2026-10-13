# Architecture

## Core Components

- Spring Boot REST API
- PostgreSQL as the durable source of truth
- Redis for redirect caching and rate limiting
- Flyway for database migrations

## Core Endpoints

- POST /api/v1/urls
- GET /{shortCode}
- GET /api/v1/urls/{shortCode}/analytics

## URL Storage

Each shortened URL stores:

- internal ID
- unique 8-character Base62 short code
- original URL
- creation timestamp

## Analytics

Redirect events are stored separately in `click_events`.

This allows analytics to remain append-only and avoids maintaining a hot
click-count field on the URL record.

## Short-Code Generation

Short codes are generated randomly using Base62.

The database unique constraint is the final authority for collision
detection. Generation is retried if a collision occurs.

## Caching

Redirects use a cache-aside strategy:

1. Look up the short code in Redis.
2. On cache hit, return the cached original URL.
3. On cache miss, query PostgreSQL.
4. Populate Redis before returning the result.

PostgreSQL remains the source of truth.

## Rate Limiting

URL creation is rate-limited per client IP using Redis.

Initial target:

20 creations per minute.

## Database Indexing

- Unique index on `urls.short_code`
- Composite index on `click_events(url_id, clicked_at DESC)`