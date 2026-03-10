package com.project.back_end.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

// Explanation of the ValidationFailed Class
// The ValidationFailed class is a custom exception handler that handles validation errors in a Spring Boot application. It is annotated with @RestControllerAdvice, which makes it a global exception handler for REST controllers.

// This class handles the MethodArgumentNotValidException which occurs when a validation fails during the binding of request parameters or request body fields to the method parameters in a controller. Typically, this happens when input data does not meet the constraints defined by annotations such as @NotNull, @Size, @Email, and so on, in the model class.

// Key Points
// @RestControllerAdvice
// This annotation is a combination of @ControllerAdvice and @ResponseBody, which makes it a global exception handler that can return the response directly as JSON (or any other format) in case of errors.
@RestControllerAdvice
public class ValidationFailed {

// Exception Handler Method
// The @ExceptionHandler(MethodArgumentNotValidException.class) annotation specifies that this method will handle exceptions of type MethodArgumentNotValidException. This exception is thrown when a validation error occurs on the request body (such as when data in a @RequestBody doesn't match the required constraints).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
// Handling Validation Errors
// The method handleValidationException is invoked when a MethodArgumentNotValidException is thrown.
// Inside the method, the exception object (ex) provides access to the binding result of the validation errors, which includes the field errors (for example, which fields failed validation).
        Map<String, String> errors = new HashMap<>();
        
// Creating the Error Response
// The FieldError object contains information about the specific field that failed validation and the corresponding error message.
// We loop through all the field errors in the exception and map them to a Map<String, String>, where the key is "message" and the value is the actual error message associated with the field.
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put("message", error.getDefaultMessage());
        }

// Returning the Response
// After processing all validation errors, the method returns a ResponseEntity with an HTTP status of BAD_REQUEST (400) and a body containing the validation error messages.
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
