package study.security.basic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {

	@GetMapping("/")
	public String index() {
		return "home";
	}

	@GetMapping("/user")
	public String user() {
		return "user";
	}

	@GetMapping("/admin/pay")
	public String adminPay() {
		return "admin";
	}

	@GetMapping("/admin/**")
	public String admin() {
		return "admin & sys";
	}

	@GetMapping("/loginPage")
	public String loginPage() {
		return "login";
	}

	@GetMapping("/denied")
	public String denied() {
		return "Access is denied";
	}
}