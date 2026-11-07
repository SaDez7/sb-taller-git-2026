package py.edu.uc.lp3.sb_taller_git_2026.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

@MappedSuperclass
public abstract class Mob extends Entidad {
	@Transient
	@JsonIgnore
	private ComportamientoIA ia;

	protected Mob() {
		super();
	}

	public Mob(String id, int saludMaxima) {
		super(id, saludMaxima);
	}

	public List<Object> generarBotin() {
		return new ArrayList<>();
	}

	public void deambular() {
		System.out.println(getId() + " deambula sin rumbo.");
	}

	public ComportamientoIA getIa() {
		return ia;
	}

	public void setIa(ComportamientoIA ia) {
		this.ia = ia;
	}
}
