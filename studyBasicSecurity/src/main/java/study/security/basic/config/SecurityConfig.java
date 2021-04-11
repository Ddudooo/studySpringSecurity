package study.security.basic.config;

import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

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

	/**
	 * @param http 보안 설정 객체
	 * @see HttpSecurity
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()//인증 요청 검사
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
				response.sendRedirect("/");
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
			.maxSessionsPreventsLogin(false);

		http
			.sessionManagement()
			.sessionFixation().changeSessionId();
	}
}