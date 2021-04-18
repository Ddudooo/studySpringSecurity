package study.studysecurity.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import study.studysecurity.domain.dto.AccountDto;
import study.studysecurity.domain.entity.Account;
import study.studysecurity.domain.service.UserService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

	private final ModelMapper modelMapper;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	@GetMapping("/mypage")
	public String myPage() {
		return "user/mypage";
	}

	@GetMapping("/users")
	public String createUser() {
		return "user/login/register";
	}

	@PostMapping("/users")
	public String createUser(AccountDto accountDto) {
		Account account = modelMapper.map(accountDto, Account.class);
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		userService.createUser(account);
		return "redirect:/";
	}
}