package study.studysecurity.global.config.security.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {

		String errMsg = "Invalid username or password";

		if (exception instanceof BadCredentialsException) {
			errMsg = "Invalid username or password";
		} else if (exception instanceof InsufficientAuthenticationException) {
			errMsg = "Invalid secret Key";
		}

		setDefaultFailureUrl("/login?error=true&exception=" + errMsg);

		super.onAuthenticationFailure(request, response, exception);
	}
}