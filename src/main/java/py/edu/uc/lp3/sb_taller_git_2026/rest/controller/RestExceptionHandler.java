package py.edu.uc.lp3.sb_taller_git_2026.rest.controller;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.CompaneroNoDisponibleException;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ReglaDeJuegoException;
import py.edu.uc.lp3.sb_taller_git_2026.domain.exception.ValidacionCompaneroException;

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

	@ExceptionHandler(ValidacionCompaneroException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorCompaneroResponse validacionCompanero(ValidacionCompaneroException error) {
		return new ErrorCompaneroResponse("validacion_companero", error.getMessage(), error.getApi(),
				error.getStatus(), resumir(error.getDetalle()));
	}

	@ExceptionHandler(CompaneroNoDisponibleException.class)
	@ResponseStatus(HttpStatus.BAD_GATEWAY)
	public ErrorCompaneroResponse companeroNoDisponible(CompaneroNoDisponibleException error) {
		return new ErrorCompaneroResponse("companero_no_disponible", error.getMessage(), error.getApi(),
				null, resumir(error.getDetalle()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse datosInvalidos(IllegalArgumentException error) {
		return new ErrorResponse("argumento_invalido", error.getMessage());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse cuerpoIlegible(HttpMessageNotReadableException error) {
		return new ErrorResponse("json_invalido", "El cuerpo de la solicitud no es un JSON válido.");
	}

	@Schema(description = "Respuesta estándar de error de la API.")
	public record ErrorResponse(String tipo, String mensaje) {
	}

	@Schema(description = "Error atribuido a la API de un compañero.")
	public record ErrorCompaneroResponse(
			String tipo,
			String mensaje,
			String api,
			Integer statusCompanero,
			String detalle) {
	}

	private static String resumir(String detalle) {
		if (detalle == null) {
			return null;
		}
		String limpio = detalle.trim();
		return limpio.length() <= 500 ? limpio : limpio.substring(0, 500);
	}
}
