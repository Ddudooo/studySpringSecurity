package study.studysecurity.global.config.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import study.studysecurity.global.config.security.filter.AjaxLoginProcessingFilter;
import study.studysecurity.global.config.security.provider.AjaxAuthenticationProvider;
import study.studysecurity.global.config.security.service.CustomUserDetailService;

@Configuration
@Order(0)
public class AjaxSecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	public AjaxSecurityConfig(
		CustomUserDetailService userDetailsService,
		PasswordEncoder passwordEncoder
	) {
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(ajaxAuthenticationProvider());
	}

	@Bean
	public AuthenticationProvider ajaxAuthenticationProvider() {
		return new AjaxAuthenticationProvider(userDetailsService, passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.antMatcher("/api/**")
			.authorizeRequests()
			.anyRequest().authenticated()
			.and()
			.addFilterBefore(ajaxLoginProcessingFilter(),
				UsernamePasswordAuthenticationFilter.class);

		http.csrf().disable();
	}

	@Bean
	public AjaxLoginProcessingFilter ajaxLoginProcessingFilter() throws Exception {
		AjaxLoginProcessingFilter ajaxLoginProcessingFilter = new AjaxLoginProcessingFilter();
		ajaxLoginProcessingFilter.setAuthenticationManager(authenticationManagerBean());
		return ajaxLoginProcessingFilter;
	}
}