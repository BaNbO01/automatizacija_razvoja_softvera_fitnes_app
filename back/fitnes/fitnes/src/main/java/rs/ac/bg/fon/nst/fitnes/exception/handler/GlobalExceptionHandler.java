/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.exception.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;


import java.util.HashMap;
import java.util.Map;
import rs.ac.bg.fon.nst.fitnes.dto.ApiResponse;
import rs.ac.bg.fon.nst.fitnes.exception.DuplicateEntryException;
import rs.ac.bg.fon.nst.fitnes.exception.FileUploadException;
import rs.ac.bg.fon.nst.fitnes.exception.ResourceNotFoundException;
import rs.ac.bg.fon.nst.fitnes.exception.UnauthorizedAccessException;


@ControllerAdvice
public class GlobalExceptionHandler {

  
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ApiResponse apiResponse = new ApiResponse(false, ex.getMessage(), null, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

  
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex, WebRequest request) {
        ApiResponse apiResponse = new ApiResponse(false, ex.getMessage(), null, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
    }

   
    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ApiResponse> handleDuplicateEntryException(DuplicateEntryException ex, WebRequest request) {
        ApiResponse apiResponse = new ApiResponse(false, ex.getMessage(), null, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
    }

    
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ApiResponse> handleFileUploadException(FileUploadException ex, WebRequest request) {
        ApiResponse apiResponse = new ApiResponse(false, ex.getMessage(), null, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

   
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse apiResponse = new ApiResponse(false, "Validacija neuspešna", errors, "Validacione greške");
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

   
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex, WebRequest request) {
        ApiResponse apiResponse = new ApiResponse(false, "Došlo je do interne serverske greške.", null, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
