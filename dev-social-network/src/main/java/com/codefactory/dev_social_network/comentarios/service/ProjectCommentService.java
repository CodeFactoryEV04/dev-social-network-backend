package com.codefactory.dev_social_network.comentarios.service;

import com.codefactory.dev_social_network.comentarios.dto.CommentResponse;
import com.codefactory.dev_social_network.comentarios.dto.CreateCommentRequest;
import com.codefactory.dev_social_network.comentarios.dto.PublishedCommentResponse;
import com.codefactory.dev_social_network.comentarios.entity.ComentarioProyecto;
import com.codefactory.dev_social_network.comentarios.entity.UsuarioRef;
import com.codefactory.dev_social_network.comentarios.repository.ComentarioProyectoRepository;
import com.codefactory.dev_social_network.comentarios.repository.ProyectoRefRepository;
import com.codefactory.dev_social_network.comentarios.repository.UsuarioRefRepository;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectCommentService {

    private static final String MENSAJE_PUBLICADO = "Comentario publicado";

    private final ProyectoRefRepository proyectoRefRepository;
    private final ComentarioProyectoRepository comentarioProyectoRepository;
    private final UsuarioRefRepository usuarioRefRepository;

    public ProjectCommentService(
            ProyectoRefRepository proyectoRefRepository,
            ComentarioProyectoRepository comentarioProyectoRepository,
            UsuarioRefRepository usuarioRefRepository
    ) {
        this.proyectoRefRepository = proyectoRefRepository;
        this.comentarioProyectoRepository = comentarioProyectoRepository;
        this.usuarioRefRepository = usuarioRefRepository;
    }

    @Transactional
    public PublishedCommentResponse createComment(UUID idUsuario, UUID idProyecto, CreateCommentRequest request) {
        verificarProyectoExiste(idProyecto);

        ComentarioProyecto comentarioGuardado = comentarioProyectoRepository.save(
                new ComentarioProyecto(idProyecto, idUsuario, request.getComentario())
        );

        String username = usuarioRefRepository.findById(idUsuario)
                .map(UsuarioRef::getUsername)
                .orElse(null);

        return new PublishedCommentResponse(
                MENSAJE_PUBLICADO,
                mapToResponse(comentarioGuardado, username)
        );
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listComments(UUID idProyecto) {
        verificarProyectoExiste(idProyecto);

        List<ComentarioProyecto> comentarios = comentarioProyectoRepository
                .findByIdProyectoOrderByFechaCreacionAsc(idProyecto);

        Set<UUID> idsAutores = comentarios.stream()
                .map(ComentarioProyecto::getIdUsuario)
                .collect(Collectors.toSet());

        Map<UUID, String> usernames = new HashMap<>();
        usuarioRefRepository.findAllById(idsAutores)
                .forEach(usuario -> usernames.put(usuario.getId(), usuario.getUsername()));

        return comentarios.stream()
                .map(comentario -> mapToResponse(comentario, usernames.get(comentario.getIdUsuario())))
                .toList();
    }

    private void verificarProyectoExiste(UUID idProyecto) {
        if (!proyectoRefRepository.existsById(idProyecto)) {
            throw new BusinessException(
                    "PROYECTO_NO_ENCONTRADO",
                    "El proyecto no existe",
                    HttpStatus.NOT_FOUND
            );
        }
    }

    private CommentResponse mapToResponse(ComentarioProyecto comentario, String username) {
        return new CommentResponse(
                comentario.getId(),
                comentario.getIdUsuario(),
                username,
                comentario.getComentario(),
                comentario.getFechaCreacion()
        );
    }
}