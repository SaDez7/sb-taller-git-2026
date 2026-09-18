package py.edu.uc.lp3.sb_taller_git_2026.minecraft;

public class Jugador extends Entidad {
	private Inventario inventario;
	private int experiencia;

	public Jugador(String id, int saludMaxima) {
		super(id, saludMaxima);
		this.inventario = new Inventario();
	}

	public void interactuar(Entidad entidad) {
		System.out.println(getId() + " interactua con " + entidad.getId());
	}

	public void construir(Bloque bloque) {
		System.out.println(getId() + " coloca un bloque de " + bloque.getTipo());
	}

	public Inventario getInventario() {
		return inventario;
	}

	public void setInventario(Inventario inventario) {
		this.inventario = inventario;
	}

	public int getExperiencia() {
		return experiencia;
	}

	public void setExperiencia(int experiencia) {
		this.experiencia = experiencia;
	}
}
