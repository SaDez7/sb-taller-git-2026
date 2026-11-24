package py.edu.uc.lp3.sb_taller_git_2026.domain.exception;

public class ValidacionCompaneroException extends DominioException {

	private final String api;
	private final int status;
	private final String detalle;

	public ValidacionCompaneroException(String api, int status, String detalle) {
		super("La API de " + api + " rechazó la validación con " + status + ".");
		this.api = api;
		this.status = status;
		this.detalle = detalle;
	}

	public String getApi() {
		return api;
	}

	public int getStatus() {
		return status;
	}

	public String getDetalle() {
		return detalle;
	}
}
