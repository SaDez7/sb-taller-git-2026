package py.edu.uc.lp3.sb_taller_git_2026.domain;

public abstract class Entidad {
	private String id;
	private Vector posicion;
	private int salud;
	private int saludMaxima;

	public Entidad(String id, int saludMaxima) {
		this.id = id;
		this.saludMaxima = saludMaxima;
		this.salud = saludMaxima;
		this.posicion = new Vector();
	}

	/**
	 * Manera en que esta entidad ataca a un objetivo.
	 *
	 * <p>Es el comportamiento comun a toda la jerarquia pero no puede
	 * resolverse aqui: cada rama decide a su modo como golpea. Un
	 * {@link Hostil} delega en su estrategia {@link AtacaJugador}, un
	 * {@link Pacifico} se niega a atacar y un {@link Jugador} pega con
	 * la mano. Quien llama al metodo no necesita saber de que tipo
	 * concreto se trata.</p>
	 *
	 * @param objetivo entidad que recibe el ataque
	 */
	public abstract void atacar(Entidad objetivo);

	public void moverse() {
		System.out.println(getId() + " se mueve.");
	}

	public void recibirDano(int cantidad) {
		this.salud = Math.max(0, this.salud - cantidad);
	}

	public void curar(int cantidad) {
		this.salud = Math.min(getSaludMaxima(), this.salud + cantidad);
	}

	public boolean estaVivo() {
		return salud > 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Vector getPosicion() {
		return posicion;
	}

	public void setPosicion(Vector posicion) {
		this.posicion = posicion;
	}

	public int getSalud() {
		return salud;
	}

	public void setSalud(int salud) {
		this.salud = salud;
	}

	public int getSaludMaxima() {
		return saludMaxima;
	}

	public void setSaludMaxima(int saludMaxima) {
		this.saludMaxima = saludMaxima;
	}
}
