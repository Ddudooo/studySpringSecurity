package study.studysecurity.global.config.security.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class FormWebAuthenticationDetails extends WebAuthenticationDetails {

	@JsonIgnore
	private static final long serialVersionUID = 7336629925077595611L;

	private String secretKey;

	/**
	 * Records the remote address and will also set the session Id if a session already exists (it
	 * won't create one).
	 *
	 * @param request that the authentication request was received from
	 */
	public FormWebAuthenticationDetails(HttpServletRequest request) {
		super(request);
		secretKey = request.getParameter("secret_key");
	}

	public String getSecretKey() {
		return secretKey;
	}
}