package study.studysecurity.global.config.security.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import study.studysecurity.global.config.security.service.AccountContext;
import study.studysecurity.global.config.security.token.AjaxAuthenticationToken;

public class AjaxAuthenticationProvider implements AuthenticationProvider {

	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	public AjaxAuthenticationProvider(
		UserDetailsService userDetailsService,
		PasswordEncoder passwordEncoder) {
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication)
		throws AuthenticationException {
		String username = authentication.getName();
		String password = String.valueOf(authentication.getCredentials());

		AccountContext userDetails = (AccountContext) userDetailsService
			.loadUserByUsername(username);

		if (!passwordEncoder.matches(password, userDetails.getAccount().getPassword())) {
			throw new BadCredentialsException("BadCredentialsException!");
		}

		return new AjaxAuthenticationToken(userDetails.getAccount(), null,
			userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(AjaxAuthenticationToken.class);
	}
}