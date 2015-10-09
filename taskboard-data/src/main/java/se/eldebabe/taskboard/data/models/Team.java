package se.eldebabe.taskboard.data.models;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "teams")
public class Team extends AbstractEntity {

	@Column(name = "name", unique = true)
	private String name;

	@OneToMany(mappedBy = "team", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private Collection<User> users;

	public void setName(String name) {
		this.name = name;
	}

	public void setUsers(Collection<User> users) {
		this.users = users;
	}

	protected Team() {
	}

	public Team(String teamName) {
		this.name = teamName;
		users = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public User addUser(User user) {
		users.add(user);
		return user;
	}

	public Collection<User> getUsers() {
		return users;
	}

}
