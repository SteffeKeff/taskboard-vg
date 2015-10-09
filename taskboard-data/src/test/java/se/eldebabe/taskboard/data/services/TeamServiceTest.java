package se.eldebabe.taskboard.data.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import se.eldebabe.taskboard.data.models.*;

public final class TeamServiceTest {

	private static AnnotationConfigApplicationContext context;
	private static TeamService teamService;
	private static UserService userService;
	private static ArrayList<Team> teams;
	private Team team;

	@BeforeClass
	public static void setupConfigs() {
		context = new AnnotationConfigApplicationContext();
		context.scan("se.eldebabe.taskboard.data.configs");
		context.refresh();
		teams = new ArrayList<>();
		teamService = context.getBean(TeamService.class);
		userService = context.getBean(UserService.class);
	}

	@Test
	public void assertThatTeamIsSavable() {
		team = new Team("deldebabe0");
		assertThat("Added Team should be returned", team, is(teamService.saveTeam(team)));
	}

	@Test
	public void assertThatTeamCanBeDeleted() {
		team = new Team("deldebabe1");
		User user = new User("id", "name", "firstname", "lastname");
		team.addUser(user);
		teamService.saveTeam(team);
		teamService.delete(team.getId());
		assertThat("teamService cant find team", null, is(teamService.findTeamByName(team.getName())));
	}

	@Test
	public void assertThatTeamCanBeUpdated() {
		team = new Team("deldebabe2");
		Team updatedTeam = teamService.updateTeam(team);
		assertThat("Team is updated", team, is(updatedTeam));
	}

	@Test
	public void assertThatAllTeamesCanBeFetched() {
		team = new Team("deldebabe3");
		Team team2 = new Team("team2");
		Team team3 = new Team("team3");
		teamService.saveTeam(team);
		teamService.saveTeam(team2);
		teamService.saveTeam(team3);

		teams = (ArrayList<Team>) teamService.findAllTeams();
		assertThat("Team count matches", 4, is(teams.size()));
	}

	/* //// These tests has relations //// */

	@Test
	public void assertThatUserCanBeAddedToTeam() {
		team = new Team("deldebabe4");
		teamService.saveTeam(team);

		User user = new User("blabla", "userName", "firstName", "lastName");
		user.setTeam(team);
		userService.saveUser(user);
		assertThat("user is in team", 1, is(teamService.findTeamByName(team.getName()).getUsers().size()));
	}

	@Test
	public void assertThatAllUsersInATeamCanBeFetched() {
		Team team1 = new Team("team1");
		teamService.saveTeam(team1);
		User user1 = new User("hejhej1", "userName1", "firstName77", "lastName77");
		User user2 = new User("hejhej2", "userName2", "firstName88", "lastName88");
		User user3 = new User("hejhej3", "userName3", "firstName99", "lastName99");
		user1.setTeam(team1);
		user2.setTeam(team1);
		user3.setTeam(team1);
		userService.saveUser(user1);
		userService.saveUser(user2);
		userService.saveUser(user3);

		Team team2 = new Team("team22");
		teamService.saveTeam(team2);
		User user4 = new User("tjatja1", "userName11", "firstName11", "lastName11");
		User user5 = new User("tjatja2", "userName22", "firstName22", "lastName22");
		User user6 = new User("tjatja3", "userName33", "firstName33", "lastName33");
		user4.setTeam(team2);
		user5.setTeam(team2);
		user6.setTeam(team2);
		userService.saveUser(user4);
		userService.saveUser(user5);
		userService.saveUser(user6);

		assertThat("Team2 should contain 3 members", 3, is(teamService.findUsersInTeam(team1.getId()).size()));
	}

	@Test
	public void assertThatAllWorkingItemsInATeamCanBeFetched() {
		Team team3 = new Team("teamELITE");
		team3 = teamService.saveTeam(team3);
		User user1 = new User("hejhej111", "userName111", "firstName777", "lastName777");
		User user2 = new User("hejhej222", "userName222", "firstName888", "lastName888");
		User user3 = new User("hejhej333", "userName333", "firstName999", "lastName999");
		user1.setTeam(team3);
		user2.setTeam(team3);
		user3.setTeam(team3);
		user1 = userService.saveUser(user1);
		user2 = userService.saveUser(user2);
		user3 = userService.saveUser(user3);
		WorkItem w1 = new WorkItem("Task1", "Task11");
		WorkItem w2 = new WorkItem("Task2", "Task12");
		WorkItem w3 = new WorkItem("Task3", "Task13");
		WorkItem w4 = new WorkItem("Task4", "Task14");
		WorkItem w5 = new WorkItem("Task5", "Task15");
		user1.addWorkItem(w1);
		user1.addWorkItem(w2);
		user2.addWorkItem(w3);
		user3.addWorkItem(w4);
		user3.addWorkItem(w5);
		userService.updateUser(user1);
		userService.updateUser(user2);
		userService.updateUser(user3);

		assertThat("Team2 should contain 5 work items", 5, is(teamService.findWorkItemsInTeam(team3.getId()).size()));
	}

	@AfterClass
	public static void tearDown() {
		context.close();
	}

}