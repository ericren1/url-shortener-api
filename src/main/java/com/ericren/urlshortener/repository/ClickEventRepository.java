package com.ericren.urlshortener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ericren.urlshortener.entity.ClickEvent;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    long countByUrlId(Long urlId);

    Optional<ClickEvent> findTopByUrlIdOrderByClickedAtDesc(Long urlId);
}