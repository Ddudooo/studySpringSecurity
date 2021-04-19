package study.studysecurity.global.config.security.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	public CustomAccessDeniedHandler() {
		this.errPage = "/denied";
	}

	private String errPage;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException, ServletException {
		String deniedUrl = errPage + "?exception=" + accessDeniedException.getMessage();
		response.sendRedirect(deniedUrl);
	}

	public void setErrPage(String errPage) {
		this.errPage = errPage;
	}
}