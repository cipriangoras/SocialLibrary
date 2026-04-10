package app.SocialLibraryAPI.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.executable.ValidateOnExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception e){
        logger.error("Handle exception", e);

        var errorDTO = new ErrorResponseDTO(
                "Internal server error",
                        e.getMessage(),
                        LocalDateTime.now()
                );
        return ResponseEntity.status(500).body(errorDTO);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(EntityNotFoundException e) {
        logger.error("Handle entityNotFoundException", e);

        var errorDto = new ErrorResponseDTO(
                "Entity not found",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(404).body(errorDto);
    }

    @ExceptionHandler(exception = {
            MethodArgumentNotValidException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(Exception e){
        logger.error("Handle Bad Request", e);

        var errorDto = new ErrorResponseDTO(
                "Bad Request",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(400).body(errorDto);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        logger.error("Handle DataIntegrityViolationException", e);

        var errorDto = new ErrorResponseDTO(
                "Duplicate / constraint violation",
                "Resource already exists or violates a database constraint.",
                LocalDateTime.now()
        );

        return ResponseEntity.status(409).body(errorDto);
    }
}
