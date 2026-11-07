package py.edu.uc.lp3.sb_taller_git_2026.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import py.edu.uc.lp3.sb_taller_git_2026.domain.Entidad;

public interface EntidadRepository extends JpaRepository<Entidad, String> {

	List<Entidad> findBySaludGreaterThan(int salud);
}
