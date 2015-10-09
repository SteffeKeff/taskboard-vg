package se.eldebabe.taskboard.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import se.eldebabe.taskboard.data.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

	List<User> findByFirstName(String firstName);

	List<User> findByLastName(String lastName);

	User findByUserName(String userName);

	User findByUserId(String userId);

	@Transactional
	User deleteByUserId(String userId);
}
