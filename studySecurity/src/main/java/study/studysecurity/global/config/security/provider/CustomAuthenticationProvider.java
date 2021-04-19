package study.studysecurity.global.config.security.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import study.studysecurity.global.config.security.common.FormWebAuthenticationDetails;
import study.studysecurity.global.config.security.service.AccountContext;

public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	public CustomAuthenticationProvider(
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

		FormWebAuthenticationDetails details = (FormWebAuthenticationDetails) authentication
			.getDetails();

		String secretKey = details.getSecretKey();
		if (secretKey == null || !"secret".equals(secretKey)) {
			throw new InsufficientAuthenticationException("InsufficientAuthenticationException!");
		}

		UsernamePasswordAuthenticationToken authenticationToken
			= new UsernamePasswordAuthenticationToken(userDetails.getAccount(), null,
			userDetails.getAuthorities());

		return authenticationToken;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}