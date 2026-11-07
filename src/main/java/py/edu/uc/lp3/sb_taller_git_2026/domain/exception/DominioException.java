package py.edu.uc.lp3.sb_taller_git_2026.domain.exception;

public abstract class DominioException extends RuntimeException {

	protected DominioException(String mensaje) {
		super(mensaje);
	}
}
