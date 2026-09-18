package py.edu.uc.lp3.sb_taller_git_2026.minecraft;

import java.util.ArrayList;
import java.util.List;

public class Inventario {
	private List<Bloque> items = new ArrayList<>();

	public void agregar(Bloque bloque) {
		items.add(bloque);
	}

	public void remover(Bloque bloque) {
		items.remove(bloque);
	}

	public List<Bloque> getItems() {
		return items;
	}

	public void setItems(List<Bloque> items) {
		this.items = items;
	}
}
