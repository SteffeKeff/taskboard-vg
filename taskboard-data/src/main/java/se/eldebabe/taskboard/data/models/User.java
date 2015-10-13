package se.eldebabe.taskboard.data.models;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import se.eldebabe.taskboard.data.services.PasswordHash;

@Entity
@Table(name = "users")
public class User extends AbstractEntity {

	@Column(name = "user_id", unique = true)
	private String userId;

	@Column(name = "user_name", unique = true)
	private String userName;
	
	@Column
	private String password;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@ManyToOne
	@JoinColumn(name = "team_id")
	private Team team;

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
	private Collection<WorkItem> workItems;

	protected User() {
	}
	
	public User(String userId, String userName, String password, String firstName, String lastName) {
		this.userId = userId;
		this.userName = userName;
		try{
			this.password = PasswordHash.createHash(password);
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}catch(InvalidKeySpecException e){
			e.printStackTrace();
		}
		this.firstName = firstName;
		this.lastName = lastName;
		workItems = new ArrayList<>();
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setWorkItems(Collection<WorkItem> workItems) {
		this.workItems = workItems;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}
	
	public String getPassword(){
		return password;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Team getTeam() {
		return team;
	}

	public Collection<WorkItem> getWorkItems() {
		return workItems;
	}

	public WorkItem addWorkItem(WorkItem workItem) {
		workItems.add(workItem);
		return workItem;
	}

	public Team setTeam(Team team) {
		this.team = team;
		return this.team;
	}
}
