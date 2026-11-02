package com.eventcloud.controller;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.*;
@RestControllerAdvice
public class ApiExceptionHandler {
 public record ErrorResponse(Instant timestamp,int status,String error,String path,Map<String,String> validationErrors){}
 @ExceptionHandler(ResponseStatusException.class) public ResponseEntity<ErrorResponse> status(ResponseStatusException ex,jakarta.servlet.http.HttpServletRequest req){int s=ex.getStatusCode().value();return ResponseEntity.status(ex.getStatusCode()).body(new ErrorResponse(Instant.now(),s,ex.getReason()==null?"Request failed":ex.getReason(),req.getRequestURI(),Map.of()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex,jakarta.servlet.http.HttpServletRequest req){Map<String,String> errors=new LinkedHashMap<>();ex.getBindingResult().getFieldErrors().forEach(e->errors.putIfAbsent(e.getField(),e.getDefaultMessage()));return ResponseEntity.badRequest().body(new ErrorResponse(Instant.now(),400,"Validation failed",req.getRequestURI(),errors));}
 @ExceptionHandler(Exception.class) public ResponseEntity<ErrorResponse> other(Exception ex,jakarta.servlet.http.HttpServletRequest req){return ResponseEntity.status(500).body(new ErrorResponse(Instant.now(),500,"Unexpected server error",req.getRequestURI(),Map.of()));}
}
