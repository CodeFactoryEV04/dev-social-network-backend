package com.codefactory.dev_social_network.usuarios.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codefactory.dev_social_network.shared.config.CurrentUserPort;
import com.codefactory.dev_social_network.usuarios.dto.FollowResponseDTO;
import com.codefactory.dev_social_network.usuarios.dto.FollowUserRequestDTO;
import com.codefactory.dev_social_network.usuarios.dto.FollowedUserDTO;
import com.codefactory.dev_social_network.usuarios.interfaces.FollowUserUseCase;
import com.codefactory.dev_social_network.usuarios.interfaces.ListFollowingUseCase;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/following")
public class UserFollowController {

    private final FollowUserUseCase followUserUseCase;
    private final ListFollowingUseCase listFollowingUseCase;
    private final CurrentUserPort currentUserPort;

    public UserFollowController(FollowUserUseCase followUserUseCase,
                                ListFollowingUseCase listFollowingUseCase,
                                CurrentUserPort currentUserPort) {
        this.followUserUseCase = followUserUseCase;
        this.listFollowingUseCase = listFollowingUseCase;
        this.currentUserPort = currentUserPort;
    }

    @PostMapping
    public ResponseEntity<FollowResponseDTO> follow(@Valid @RequestBody FollowUserRequestDTO request) {
        UUID followerId = currentUserPort.currentUserId();
        FollowedUserDTO followed = followUserUseCase.follow(followerId, request.target());
        String name = followed.username() != null ? followed.username() : followed.fullName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new FollowResponseDTO("Estás siguiendo a " + name, followed));
    }

    @GetMapping
    public List<FollowedUserDTO> list() {
        return listFollowingUseCase.list(currentUserPort.currentUserId());
    }
}