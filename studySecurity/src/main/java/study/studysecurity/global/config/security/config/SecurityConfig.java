package study.studysecurity.global.config.security.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import study.studysecurity.global.config.security.common.FormAuthenticationDetailsSource;
import study.studysecurity.global.config.security.handler.CustomAccessDeniedHandler;
import study.studysecurity.global.config.security.handler.CustomAuthenticationFailureHandler;
import study.studysecurity.global.config.security.handler.CustomAuthenticationSuccessHandler;
import study.studysecurity.global.config.security.provider.CustomAuthenticationProvider;
import study.studysecurity.global.config.security.service.CustomUserDetailService;

@Order(1)
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final FormAuthenticationDetailsSource authenticationDetailsSource;
	private final AuthenticationSuccessHandler authenticationSuccessHandler;
	private final AuthenticationFailureHandler authenticationFailureHandler;
	private final AccessDeniedHandler accessDeniedHandler;

	public SecurityConfig(
		CustomUserDetailService userDetailsService,
		FormAuthenticationDetailsSource authenticationDetailsSource,
		CustomAuthenticationSuccessHandler authenticationSuccessHandler,
		CustomAuthenticationFailureHandler authenticationFailureHandler,
		CustomAccessDeniedHandler accessDeniedHandler
	) {
		this.userDetailsService = userDetailsService;
		this.authenticationDetailsSource = authenticationDetailsSource;
		this.authenticationSuccessHandler = authenticationSuccessHandler;
		this.authenticationFailureHandler = authenticationFailureHandler;
		this.accessDeniedHandler = accessDeniedHandler;
	}

	@Bean
	public AuthenticationProvider authenticationProvider(
		UserDetailsService userDetailService,
		PasswordEncoder passwordEncoder
	) {
		return new CustomAuthenticationProvider(userDetailService, passwordEncoder);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//auth.userDetailsService(userDetailsService);
		auth.authenticationProvider(authenticationProvider(userDetailsService, passwordEncoder()));
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/", "/users", "user/login/**", "/login*", "/denied*").permitAll()
			.antMatchers("/mypage").hasRole("USER")
			.antMatchers("/messages").hasRole("MANAGER")
			.antMatchers("/config").hasRole("ADMIN")
			.anyRequest().authenticated()

			.and()
			.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/login_proc")
			.authenticationDetailsSource(authenticationDetailsSource)
			.defaultSuccessUrl("/")
			.successHandler(authenticationSuccessHandler)
			.failureHandler(authenticationFailureHandler)
			.permitAll()
			.and()
			.exceptionHandling()
			.accessDeniedHandler(accessDeniedHandler);
			
	}


}