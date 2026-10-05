package py.edu.uc.lp3.sb_taller_git_2026.domain;

public class Jugador extends Entidad {
	private static final int SALUD_POR_DEFECTO = 20;

	private Inventario inventario;
	private int experiencia;

	/**
	 * Jugador recien creado: sin experiencia y en el origen del mundo.
	 */
	public Jugador() {
		this("Steve");
	}

	/**
	 * Jugador con id y salud por defecto, sin experiencia.
	 */
	public Jugador(String id) {
		this(id, SALUD_POR_DEFECTO, 0, new Vector());
	}

	public Jugador(String id, int saludMaxima) {
		this(id, saludMaxima, 0, new Vector());
	}

	public Jugador(String id, int saludMaxima, int experiencia) {
		this(id, saludMaxima, experiencia, new Vector());
	}

	/**
	 * Constructor completo: valida y deja al jugador en estado valido.
	 *
	 * <p>La validacion vive aca y no en el controller: si un dato viene
	 * mal, el dominio lo rechaza con {@link IllegalArgumentException}
	 * en vez de que alguien de afuera lo "arregle".</p>
	 *
	 * @throws IllegalArgumentException si el id esta vacio, la salud
	 *         no es positiva, la experiencia es negativa o la posicion
	 *         es null
	 */
	public Jugador(String id, int saludMaxima, int experiencia, Vector posicion) {
		super(validarId(id), validarSaludMaxima(saludMaxima));
		this.experiencia = validarExperiencia(experiencia);
		setPosicion(validarPosicion(posicion));
		this.inventario = new Inventario();
	}

	private static String validarId(String id) {
		if (id == null || id.isBlank()) {
			throw new IllegalArgumentException("El id del jugador no puede estar vacio.");
		}
		return id;
	}

	private static int validarSaludMaxima(int saludMaxima) {
		if (saludMaxima <= 0) {
			throw new IllegalArgumentException(
					"La salud maxima debe ser mayor a 0, se recibio: " + saludMaxima);
		}
		return saludMaxima;
	}

	private static int validarExperiencia(int experiencia) {
		if (experiencia < 0) {
			throw new IllegalArgumentException(
					"La experiencia no puede ser negativa, se recibio: " + experiencia);
		}
		return experiencia;
	}

	private static Vector validarPosicion(Vector posicion) {
		if (posicion == null) {
			throw new IllegalArgumentException("La posicion del jugador no puede ser null.");
		}
		return posicion;
	}

	@Override
	public void atacar(Entidad objetivo) {
		objetivo.recibirDano(4);
		System.out.println(getId() + " golpea a " + objetivo.getId() + " con la mano.");
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
