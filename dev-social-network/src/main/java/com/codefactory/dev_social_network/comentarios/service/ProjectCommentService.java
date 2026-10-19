package com.codefactory.dev_social_network.comentarios.service;
 
import com.codefactory.dev_social_network.comentarios.dto.CommentResponse;
import com.codefactory.dev_social_network.comentarios.dto.CreateCommentRequest;
import com.codefactory.dev_social_network.comentarios.entity.ComentarioProyecto;
import com.codefactory.dev_social_network.comentarios.repository.ComentarioProyectoRepository;
import com.codefactory.dev_social_network.comentarios.repository.ProyectoRefRepository;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectCommentService {

    private final ProyectoRefRepository proyectoRefRepository;
    private final ComentarioProyectoRepository comentarioProyectoRepository;

    public ProjectCommentService(
            ProyectoRefRepository proyectoRefRepository,
            ComentarioProyectoRepository comentarioProyectoRepository
    ) {
        this.proyectoRefRepository = proyectoRefRepository;
        this.comentarioProyectoRepository = comentarioProyectoRepository;
    }

    @Transactional
    public CommentResponse createComment(UUID idUsuario, UUID idProyecto, CreateCommentRequest request) {
        verificarProyectoExiste(idProyecto);

        ComentarioProyecto comentario = new ComentarioProyecto(
                idProyecto,
                idUsuario,
                request.getComentario()
        );

        ComentarioProyecto comentarioGuardado = comentarioProyectoRepository.save(comentario);

        return mapToResponse(comentarioGuardado);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listComments(UUID idProyecto) {
        verificarProyectoExiste(idProyecto);

        return comentarioProyectoRepository
                .findByIdProyectoOrderByFechaCreacionAsc(idProyecto)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void verificarProyectoExiste(UUID idProyecto) {
        if (!proyectoRefRepository.existsById(idProyecto)) {
            throw new BusinessException(
                    "PROYECTO_NO_ENCONTRADO",
                    "El proyecto indicado no existe",
                    HttpStatus.NOT_FOUND
            );
        }
    }

    private CommentResponse mapToResponse(ComentarioProyecto comentario) {
        return new CommentResponse(
                comentario.getId(),
                comentario.getIdUsuario(),
                comentario.getComentario(),
                comentario.getFechaCreacion()
        );
    }
}