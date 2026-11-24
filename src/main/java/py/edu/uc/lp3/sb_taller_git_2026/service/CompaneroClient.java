package py.edu.uc.lp3.sb_taller_git_2026.service;

import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA2Request;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA2Response;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA3Request;
import py.edu.uc.lp3.sb_taller_git_2026.service.dto.ValidacionA3Response;

public interface CompaneroClient {

	ValidacionA2Response validarA2(ValidacionA2Request request);

	ValidacionA3Response validarA3(Long creeperId, ValidacionA3Request request);
}
