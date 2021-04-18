package study.studysecurity.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.studysecurity.domain.entity.Account;
import study.studysecurity.domain.repo.UserRepo;
import study.studysecurity.domain.service.UserService;

@Slf4j
@Service("userService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepo userRepo;

	@Override
	@Transactional
	public void createUser(Account account) {
		userRepo.save(account);
	}
}