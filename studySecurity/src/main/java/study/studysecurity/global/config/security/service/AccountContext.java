package study.studysecurity.global.config.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import study.studysecurity.domain.entity.Account;


public class AccountContext extends User {

	@JsonIgnore
	private static final long serialVersionUID = 3197076626066938196L;

	private final Account account;

	public AccountContext(Account account,
		Collection<? extends GrantedAuthority> authorities) {
		super(account.getUsername(), account.getPassword(), authorities);
		this.account = account;
	}
}