package com.projetoSpringApiRestfull;

import java.sql.SQLException;
import java.util.List;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@ControllerAdvice
public class ControllerException extends ResponseEntityExceptionHandler {

	// intercepta erros mais comuns
	@ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class}) 
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		String msg = "";
		
		if(ex instanceof MethodArgumentNotValidException) {
			
			List<ObjectError> list = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();
			
			for (ObjectError objectError : list) {
				msg += objectError.getDefaultMessage() + "\n"; 
			}
			
		}else  {
			msg = ex.getMessage();
		}
		
		ObjectErro objectError = new ObjectErro();
		objectError.setError(msg);
		objectError.setCode(status.value() + " ==> " + status.getReasonPhrase());
		
		return new ResponseEntity<>(objectError, headers, status) ;
	}
	
	//Tratamento da maioria dos erros a nivel de banco de dados
	@ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class, 
		PSQLException.class, SQLException.class})
	protected ResponseEntity<Object> handleExceptionDataIntegry (Exception ex){
		
		String msg = "";
		
		if(ex instanceof DataIntegrityViolationException) {
			msg = ((DataIntegrityViolationException) ex).getCause().getCause().getMessage();
		
			} else if (ex instanceof ConstraintViolationException) {
				msg = ((ConstraintViolationException) ex).getCause().getCause().getMessage();
		
			} else if (ex instanceof PSQLException) {
				msg = ((PSQLException) ex).getCause().getCause().getMessage();
		
			} else if (ex instanceof SQLException) {
				msg = ((SQLException) ex).getCause().getCause().getMessage();
		}
		
		else { //genericos entrão aqui
			msg = ex.getMessage();
		}
		
		ObjectErro objectErro = new ObjectErro();
		objectErro.setError(msg);
		objectErro.setCode(HttpStatus.INTERNAL_SERVER_ERROR + " ==> " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		
		return new ResponseEntity<>(objectErro, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}






