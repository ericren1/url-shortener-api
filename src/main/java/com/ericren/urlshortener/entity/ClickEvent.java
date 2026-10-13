package com.ericren.urlshortener.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "click_events")
public class ClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "url_id", nullable = false)
    private Url url;

    @Column(name = "clicked_at", nullable = false)
    private Instant clickedAt;

    protected ClickEvent() {
    }

    public ClickEvent(Url url, Instant clickedAt) {
        this.url = url;
        this.clickedAt = clickedAt;
    }

    public Long getId() {
        return id;
    }

    public Url getUrl() {
        return url;
    }

    public Instant getClickedAt() {
        return clickedAt;
    }
}