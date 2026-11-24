package py.edu.uc.lp3.sb_taller_git_2026.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "escenarios_cruzados")
public class EscenarioCruzado {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "jugador_id", nullable = false, length = 100)
	private String jugadorId;

	@Column(name = "tnt_id", nullable = false, length = 100)
	private String tntId;

	@Column(name = "objetivo_a2_id", nullable = false, length = 100)
	private String objetivoA2Id;

	@Column(name = "explosion_a2_id", nullable = false)
	private Long explosionA2Id;

	@Column(name = "dano_a2", nullable = false)
	private int danoA2;

	@Column(name = "salud_a2_despues", nullable = false)
	private int saludA2Despues;

	@Column(name = "creeper_a3_id", nullable = false)
	private Long creeperA3Id;

	@Column(name = "objetivo_a3_id", nullable = false)
	private Long objetivoA3Id;

	@Column(name = "distancia_a3", nullable = false)
	private int distanciaA3;

	@Column(name = "dano_a3", nullable = false)
	private int danoA3;

	@Column(name = "vida_a3_despues", nullable = false)
	private int vidaA3Despues;

	protected EscenarioCruzado() {
	}

	public EscenarioCruzado(String jugadorId, String tntId, String objetivoA2Id, Long explosionA2Id, int danoA2,
			int saludA2Despues, Long creeperA3Id, Long objetivoA3Id, int distanciaA3, int danoA3, int vidaA3Despues) {
		this(null, jugadorId, tntId, objetivoA2Id, explosionA2Id, danoA2, saludA2Despues, creeperA3Id,
				objetivoA3Id, distanciaA3, danoA3, vidaA3Despues);
	}

	public EscenarioCruzado(Long id, String jugadorId, String tntId, String objetivoA2Id, Long explosionA2Id,
			int danoA2, int saludA2Despues, Long creeperA3Id, Long objetivoA3Id, int distanciaA3, int danoA3,
			int vidaA3Despues) {
		this.id = id;
		this.jugadorId = jugadorId;
		this.tntId = tntId;
		this.objetivoA2Id = objetivoA2Id;
		this.explosionA2Id = explosionA2Id;
		this.danoA2 = danoA2;
		this.saludA2Despues = saludA2Despues;
		this.creeperA3Id = creeperA3Id;
		this.objetivoA3Id = objetivoA3Id;
		this.distanciaA3 = distanciaA3;
		this.danoA3 = danoA3;
		this.vidaA3Despues = vidaA3Despues;
	}

	public Long getId() {
		return id;
	}

	public String getJugadorId() {
		return jugadorId;
	}

	public String getTntId() {
		return tntId;
	}

	public String getObjetivoA2Id() {
		return objetivoA2Id;
	}

	public Long getExplosionA2Id() {
		return explosionA2Id;
	}

	public int getDanoA2() {
		return danoA2;
	}

	public int getSaludA2Despues() {
		return saludA2Despues;
	}

	public Long getCreeperA3Id() {
		return creeperA3Id;
	}

	public Long getObjetivoA3Id() {
		return objetivoA3Id;
	}

	public int getDistanciaA3() {
		return distanciaA3;
	}

	public int getDanoA3() {
		return danoA3;
	}

	public int getVidaA3Despues() {
		return vidaA3Despues;
	}
}
