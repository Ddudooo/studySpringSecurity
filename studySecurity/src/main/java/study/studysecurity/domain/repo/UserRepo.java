package study.studysecurity.domain.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import study.studysecurity.domain.entity.Account;

public interface UserRepo extends JpaRepository<Account, Long> {

	Optional<Account> findByUsername(String username);
}
