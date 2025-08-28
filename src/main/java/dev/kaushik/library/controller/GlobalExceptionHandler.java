package dev.kaushik.library.controller;

import dev.kaushik.library.exception.LibraryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(LibraryException.class)
	public ResponseEntity<String> handleLibraryException(LibraryException ex) {
		ex.printStackTrace();
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		ex.printStackTrace();
		StringBuilder errors = new StringBuilder();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			errors.append(error.getDefaultMessage()).append("; "); 
		});
		return new ResponseEntity<>(errors.toString(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception ex) {
		ex.printStackTrace();
		return new ResponseEntity<>("error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}