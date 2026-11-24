package py.edu.uc.lp3.sb_taller_git_2026.domain.exception;

public class CompaneroNoDisponibleException extends DominioException {

	private final String api;
	private final String detalle;

	public CompaneroNoDisponibleException(String api, String detalle) {
		super("La API de " + api + " no estuvo disponible.");
		this.api = api;
		this.detalle = detalle;
	}

	public String getApi() {
		return api;
	}

	public String getDetalle() {
		return detalle;
	}
}
