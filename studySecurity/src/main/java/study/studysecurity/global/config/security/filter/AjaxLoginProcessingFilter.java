package study.studysecurity.global.config.security.filter;

import static org.springframework.util.StringUtils.hasText;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import study.studysecurity.domain.dto.AccountDto;
import study.studysecurity.global.config.security.token.AjaxAuthenticationToken;

public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private final ObjectMapper objectMapper = new ObjectMapper();

	public AjaxLoginProcessingFilter() {
		super(new AntPathRequestMatcher("/api/login"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
		HttpServletResponse response)
		throws AuthenticationException, IOException, ServletException {

		if (!isAjax(request)) {
			throw new IllegalStateException("Authentication is not supported");
		}
		AccountDto accountDto = objectMapper.readValue(request.getReader(), AccountDto.class);
		if (!hasText(accountDto.getUsername()) || !hasText(accountDto.getPassword())) {
			throw new IllegalArgumentException("Username or Password is empty!");
		}
		AjaxAuthenticationToken token = new AjaxAuthenticationToken(accountDto.getUsername(),
			accountDto.getPassword());

		return getAuthenticationManager().authenticate(token);
	}

	private boolean isAjax(HttpServletRequest request) {
		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			return true;
		}
		return false;
	}
}