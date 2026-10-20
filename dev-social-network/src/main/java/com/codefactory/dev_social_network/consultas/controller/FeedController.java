package com.codefactory.dev_social_network.consultas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codefactory.dev_social_network.consultas.dto.FeedItem;
import com.codefactory.dev_social_network.consultas.interfaces.GetFeedUseCase;
import com.codefactory.dev_social_network.shared.config.CurrentUserPort;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final GetFeedUseCase getFeedUseCase;
    private final CurrentUserPort currentUserPort;

    public FeedController(GetFeedUseCase getFeedUseCase, CurrentUserPort currentUserPort) {
        this.getFeedUseCase = getFeedUseCase;
        this.currentUserPort = currentUserPort;
    }

    @GetMapping
    public List<FeedItem> feed() {
        return getFeedUseCase.feedFor(currentUserPort.currentUserId());
    }
}