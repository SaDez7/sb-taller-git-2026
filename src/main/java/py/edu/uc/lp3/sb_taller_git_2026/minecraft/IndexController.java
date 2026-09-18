package py.edu.uc.lp3.sb_taller_git_2026.minecraft;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

	@GetMapping("/")
	public String index() {
		return "hola amigo";
	}
}
