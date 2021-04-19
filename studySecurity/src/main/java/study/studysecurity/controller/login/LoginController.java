package study.studysecurity.controller.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import study.studysecurity.domain.entity.Account;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String loginPage(
		@RequestParam(value = "error", required = false, defaultValue = "false") boolean isError,
		@RequestParam(value = "exception", required = false, defaultValue = "") String exceptionMsg,
		Model model
	) {
		model.addAttribute("error", isError);
		model.addAttribute("exception", exceptionMsg);
		return "user/login/login";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			new SecurityContextLogoutHandler().logout(request, response, authentication);
		}

		return "redirect:/login";
	}

	@GetMapping("/denied")
	public String accessDenied(
		@RequestParam(value = "exception", required = false) String errMsg,
		Model model
	) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account principal = (Account) authentication.getPrincipal();
		model.addAttribute("username", principal.getUsername());
		model.addAttribute("exception", errMsg);
		return "user/login/denied";
	}
}