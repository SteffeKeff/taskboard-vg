package se.eldebabe.taskboard.web.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;

import se.eldebabe.taskboard.data.models.Team;
import se.eldebabe.taskboard.data.models.User;
import se.eldebabe.taskboard.data.models.WorkItem;
import se.eldebabe.taskboard.data.services.TeamService;
import se.eldebabe.taskboard.data.services.UserService;

@Path("teams")
@Produces({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
@Consumes({ javax.ws.rs.core.MediaType.APPLICATION_JSON })
public class TeamWebService {

	private static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	private static TeamService teamService;
	private static UserService userService;
	Gson gson = new Gson();

	static {
		context.scan("se.eldebabe.taskboard.data.configs");
		context.refresh();
		teamService = context.getBean(TeamService.class);
		userService = context.getBean(UserService.class);
	}

	@GET
	@Path("/id/{id}")
	public Response getTeamById(@PathParam("id") final Long id) {
		Team team = teamService.findById(id);

		if (null != team) {
			return Response.ok(team).build();
		} else {
			return Response.noContent().build();
		}

	}

	@GET
	@Path("{name}")
	public Response getTeamByName(@PathParam("name") final String name) {
		Team team = teamService.findTeamByName(name);

		if (null != team) {
			return Response.ok(team).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}

	}

	@GET
	@Path("/id/{id}/workitems")
	public Response getAllWorkItemsInTeam(@PathParam("id") final Long id) {

		Team team = teamService.findById(id);
		HashSet<WorkItem> workItems = new HashSet<>();

		if (null != team) {
			for (User user : team.getUsers()) {
				workItems.addAll(user.getWorkItems());
			}
			ArrayList<WorkItem> workItemsList = new ArrayList<>(workItems);
			GenericEntity<List<WorkItem>> entity = new GenericEntity<List<WorkItem>>(workItemsList){
			};
			return Response.ok(entity).build();
		} else {
			return Response.noContent().build();
		}

	}
	
	@GET
	@Path("/id/{id}/users")
	public Response getAllUsersInTeam(@PathParam("id") final Long id) {

		Team team = teamService.findById(id);
		HashSet<User> users = new HashSet<>();

		if (null != team) {
			users.addAll(team.getUsers());
			
			ArrayList<User> userList = new ArrayList<>(users);
			GenericEntity<List<User>> entity = new GenericEntity<List<User>>(userList){
			};
			return Response.ok(entity).build();
		} else {
			return Response.noContent().build();
		}

	}

	@POST
	public Response saveTeam(Team team) {
		teamService.saveTeam(team);
		return Response.ok(team).build();
	}

	@PUT
	@Path("/id/{id}/{userId}")
	public Response addUsersToTeam(@PathParam("id") final Long id, @PathParam("userId") final String userID) {

		User userToBeAdded = userService.findUser(userID);

		userToBeAdded.setTeam(teamService.findById(id));
		userService.saveUser(userToBeAdded);
		Team updatedTeam = teamService.findById(id);

		return Response.ok(updatedTeam).build();
	}

	@GET
	public Response getAllTeams() {

		List<Team> teams = teamService.findAllTeams();
		if (teams == null || teams.size() == 0) {
			return Response.status(Status.NOT_FOUND).build();
		} else {
			GenericEntity<List<Team>> entity = new GenericEntity<List<Team>>(teams){
			};
			return Response.ok(entity).build();
		}
	}

	@DELETE
	@Path("{name}")
	public final Response deleteTeamByName(@PathParam("name") final String name) {

		Team teamWithUsers = teamService.findTeamByName(name);

		List<User> usersInTeam = (List<User>) teamWithUsers.getUsers();

		for (User user : usersInTeam) {
			user.setTeam(null);
			userService.updateUser(user);
		}

		List<Team> teams = teamService.deleteByName(name);

		if (teams != null) {
			return Response.ok(teams.get(0)).build();
		}
		return Response.status(Status.NOT_FOUND).build();

	}

	@DELETE
	@Path("/id/{id}")
	public final Response deleteTeamById(@PathParam("id") final Long id) {

		Team teamWithUsers = teamService.findById(id);

		List<User> usersInTeam = (List<User>) teamWithUsers.getUsers();

		for (User user : usersInTeam) {
			user.setTeam(null);
			userService.updateUser(user);
		}

		Team team = teamService.delete(id);

		if (team != null) {
			return Response.ok(team).build();
		}
		return Response.status(Status.NOT_FOUND).build();

	}

	@DELETE
	@Path("/id/{id}/users/{userId}")
	public final Response removeUserFromTeam(@PathParam("id") final Long id, @PathParam("userId") final String userId) {

		Team team = teamService.findById(id);
		User user = userService.findUser(userId);

		if (team != null && user != null) {
			user.setTeam(null);
			userService.updateUser(user);
			team = teamService.findById(id);
			return Response.ok(team).build();
		}
		return Response.status(Status.NOT_FOUND).build();

	}

}
