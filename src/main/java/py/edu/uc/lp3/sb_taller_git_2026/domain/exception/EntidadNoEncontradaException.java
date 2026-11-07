package py.edu.uc.lp3.sb_taller_git_2026.domain.exception;

public class EntidadNoEncontradaException extends DominioException {

	public EntidadNoEncontradaException(String id) {
		super("No existe una entidad con id " + id + ".");
	}
}
