package clinica.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Manejo global de excepciones para todos los controladores REST.
// Garantiza respuestas de error consistentes en formato JSON.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura errores de validación (@Valid) y devuelve la lista de campos inválidos.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        List<String> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorBody(HttpStatus.BAD_REQUEST, "Error de validación", errores));
    }

    // Captura ResponseStatusException lanzadas desde services y controllers.
    // Devuelve el código HTTP y mensaje definidos en la excepción.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(
            ResponseStatusException ex) {

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity
                .status(status)
                .body(errorBody(status, ex.getReason(), null));
    }

    // Captura cualquier excepción no controlada.
    // HTTP 500 Internal Server Error.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error interno del servidor", null));
    }

    // Helper: construye el cuerpo de error estándar.
    // "message" (en inglés) para compatibilidad con el frontend que lee b.message
    private Map<String, Object> errorBody(HttpStatus status, String mensaje, List<String> detalles) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",  status.value());
        body.put("error",   status.getReasonPhrase());
        body.put("message", mensaje);  // antes: "mensaje" — el frontend lee b.message
        if (detalles != null && !detalles.isEmpty()) {
            body.put("detalles", detalles);
        }
        return body;
    }
}
