package study.studysecurity.global.config.security.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import study.studysecurity.domain.entity.Account;
import study.studysecurity.domain.repo.UserRepo;

@Service("userDetailService")
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

	private final UserRepo repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Optional<Account> opAccount = repo.findByUsername(username);

		Account account = opAccount
			.orElseThrow(() -> new UsernameNotFoundException("Not found Username Exception!"));

		List<GrantedAuthority> roles = new ArrayList<>();
		roles.add(new SimpleGrantedAuthority(account.getRole()));

		AccountContext accountContext = new AccountContext(account, roles);

		return accountContext;
	}
}