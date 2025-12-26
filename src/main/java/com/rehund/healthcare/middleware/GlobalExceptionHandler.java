package com.rehund.healthcare.middleware;

import com.rehund.healthcare.common.exception.BadRequestException;
import com.rehund.healthcare.common.exception.ResourceNotFoundException;
import com.rehund.healthcare.common.exception.user.*;
import com.rehund.healthcare.model.error.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ErrorResponse handleGenericException(
            HttpServletRequest request,
            HttpServletResponse response,
            RuntimeException ex
    ) {
        if (
                ex instanceof AccessDeniedException
                        || ex instanceof SignatureException
                        || ex instanceof ExpiredJwtException
                        || ex instanceof AuthenticationException
        ){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return ErrorResponse.builder()
                    .code(HttpStatus.FORBIDDEN.value())
                    .message(ex.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        log.error(
                "Error happened in: {} with status code: {} and error: {}",
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage()
        );
        return ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({
            ResourceNotFoundException.class,
            RoleNotFoundException.class,
            UserNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponse handleResourceNotFoundException(
            HttpServletRequest request,
            RuntimeException ex
    ) {
        return ErrorResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({
            EmailConflictException.class,
            UsernameConflictException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ErrorResponse handleConflictException(
            HttpServletRequest request,
            RuntimeException ex
    ) {
        return ErrorResponse.builder()
                .code(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleBadRequestException(
            HttpServletRequest request,
            BadRequestException ex
    ) {
        return ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleInvalidPasswordException(
            HttpServletRequest request,
            InvalidPasswordException ex
    ) {
        return ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

}
