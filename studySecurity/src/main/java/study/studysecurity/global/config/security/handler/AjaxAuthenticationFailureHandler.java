package study.studysecurity.global.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class AjaxAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {

		String errMsg = "Invalid Username or Password";

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		if (exception instanceof BadCredentialsException) {
			errMsg = "Invalid username or password";
		} else if (exception instanceof InsufficientAuthenticationException) {
			errMsg = "Invalid secret Key";
		}

		mapper.writeValue(response.getWriter(), errMsg);
	}
}