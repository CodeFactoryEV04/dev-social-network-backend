package com.codefactory.dev_social_network.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler · BusinessException")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("H01 · Traduce BusinessException 409 a ApiErrorResponse")
    void handleBusinessException_conflicto_responde409ConErrorCode() {
        BusinessException ex = new BusinessException(
                "PROJECT_ALREADY_LIKED", "Ya le haz dado like a este proyecto", HttpStatus.CONFLICT);

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex, null);

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody().getErrorCode()).isEqualTo("PROJECT_ALREADY_LIKED");
        assertThat(response.getBody().getMessage()).isEqualTo("Ya le haz dado like a este proyecto");
        assertThat(response.getBody().getTraceId()).isNotBlank();
    }
}