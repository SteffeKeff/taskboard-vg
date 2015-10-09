package se.eldebabe.taskboard.data.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import se.eldebabe.taskboard.data.models.User;
import se.eldebabe.taskboard.data.repositories.UserRepository;

public class UserService {

	@Autowired
	private UserRepository userRepository;

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public User updateUser(User user) {
		User oldUser = findUser(user.getUserId());
		if (oldUser != null) {
			deleteUser(oldUser.getId());
			return userRepository.save(user);
		} else {
			return null;
		}
	}

	public User findUser(String userId) {
		return userRepository.findByUserId(userId);
	}

	public List<User> findByFirstname(String firstName) {
		return userRepository.findByFirstName(firstName);
	}

	public List<User> findByLastname(String lastName) {
		return userRepository.findByLastName(lastName);
	}

	public User findByUserName(String userName) {
		return userRepository.findByUserName(userName);
	}

	public void deleteUser(Long id) {
		userRepository.delete(id);
	}

	public void clearUsers() {
		userRepository.deleteAll();
	}

	public ArrayList<User> getAllUsers() {
		Iterable<User> dbUsers = userRepository.findAll();
		ArrayList<User> users = new ArrayList<>();
		for (User user : dbUsers) {
			users.add(user);
		}
		return users;
	}

	public ArrayList<User> getAllUsers(int page, int size) {
		PageRequest request = new PageRequest(page, size);

		Page<User> dbUsers = userRepository.findAll(request);
		ArrayList<User> users = new ArrayList<>();
		for (User user : dbUsers) {
			users.add(user);
		}
		return users;
	}

}
