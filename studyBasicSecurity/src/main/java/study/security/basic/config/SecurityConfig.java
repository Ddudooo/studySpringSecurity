package study.security.basic.config;

import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

/**
 * 사용자 정의 보안 기능 설정 클래스
 *
 * @see WebSecurityConfigurerAdapter
 * @see org.springframework.security.config.annotation.web.WebSecurityConfigurer
 * @see HttpSecurity
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("user").password("{noop}1111").roles("USER");

		auth.inMemoryAuthentication()
			.withUser("sys").password("{noop}1111").roles("SYS", "USER");

		auth.inMemoryAuthentication()
			.withUser("admin").password("{noop}1111").roles("ADMIN", "SYS", "USER");
	}

	/**
	 * @param http 보안 설정 객체
	 * @see HttpSecurity
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()//인증 요청 검사
			.antMatchers("/login").permitAll()
			.antMatchers("/user").hasRole("USER")
			.antMatchers("/admin/pay").hasRole("admin")
			.antMatchers("/admin/**").access("hasRole('ADMIN') or hasRole('SYS')")
			.anyRequest().authenticated();//어떠한 요청에도 인증을 받아야함

		http
			.formLogin()
			//.loginPage("/loginPage")
			.defaultSuccessUrl("/")
			.failureUrl("/login")
			.usernameParameter("userId")
			.passwordParameter("pw")
			.loginProcessingUrl("/login_proc")
			.successHandler((request, response, authentication) -> {
				System.out.println("authentication " + authentication.getName());
				RequestCache requestCache = new HttpSessionRequestCache();
				SavedRequest savedRequest = requestCache.getRequest(request, response);
				String redirectUrl = savedRequest.getRedirectUrl();
				response.sendRedirect(redirectUrl);
				//response.sendRedirect("/");
			})
			.failureHandler((request, response, exception) -> {
				System.out.println("exception " + exception.getMessage());
				response.sendRedirect("/loginPage");
			})
			.permitAll();

		http
			.logout()
			.logoutUrl("/logout")
			.logoutSuccessUrl("/login")
			.addLogoutHandler((request, response, authentication) -> {
				HttpSession session = request.getSession();
				session.invalidate();
			})
			.logoutSuccessHandler((request, response, authentication) -> {
				response.sendRedirect("/login");
			})
			.deleteCookies("remember-me", "JSESSIONID");

		http
			.rememberMe()
			.rememberMeParameter("remember")
			.tokenValiditySeconds(3600)
			.userDetailsService(userDetailsService);

		http
			.sessionManagement()
			.maximumSessions(1)
			.maxSessionsPreventsLogin(true);  // 동시 세션 관리 필터 확인

		http
			.sessionManagement()
			.sessionFixation().changeSessionId();

		http.exceptionHandling()
//			.authenticationEntryPoint((request, response, authException) -> {
//				response.sendRedirect("/login");
//			})
			.accessDeniedHandler((request, response, accessDeniedException) -> {
				response.sendRedirect("/denied");
			});
	}
}