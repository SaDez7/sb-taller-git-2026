package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ReglaDeJuegoException;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(EntidadNoEncontradaException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse noEncontrada(EntidadNoEncontradaException error) {
		return new ErrorResponse("entidad_no_encontrada", error.getMessage());
	}

	@ExceptionHandler(ReglaDeJuegoException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorResponse reglaDeJuego(ReglaDeJuegoException error) {
		return new ErrorResponse("regla_de_juego", error.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse datosInvalidos(IllegalArgumentException error) {
		return new ErrorResponse("argumento_invalido", error.getMessage());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse cuerpoIlegible(HttpMessageNotReadableException error) {
		return new ErrorResponse("json_invalido", "El cuerpo de la solicitud no es un JSON valido.");
	}

	public record ErrorResponse(String tipo, String mensaje) {
	}
}
