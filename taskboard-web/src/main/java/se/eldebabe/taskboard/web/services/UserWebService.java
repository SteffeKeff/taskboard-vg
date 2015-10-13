package se.eldebabe.taskboard.web.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import se.eldebabe.taskboard.data.models.User;
import se.eldebabe.taskboard.data.models.WorkItem;
import se.eldebabe.taskboard.data.services.UserService;
import se.eldebabe.taskboard.data.services.WorkItemService;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserWebService {

	private static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	private static UserService userService;
	private static WorkItemService workItemService;

	static {
		context.scan("se.eldebabe.taskboard.data.configs");
		context.refresh();
		userService = context.getBean(UserService.class);
		workItemService = context.getBean(WorkItemService.class);
	}

	@Context
	public UriInfo uriInfo;

	@POST
	public Response createUser(User user) {

		user = userService.saveUser(user);

		if (null != user) {
			final URI location = uriInfo.getAbsolutePathBuilder().path(user.getUserId().toString()).build();
			return Response.created(location).build();
		} else {
			return Response.status(Status.BAD_REQUEST).build();
		}

	}

	@GET
	@Path("{userId}")
	public Response getUser(@PathParam("userId") final String userId) {
		User user = userService.findUser(userId);
		if (null != user) {
			return Response.ok(user).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("username")
	public Response searchUserByUserName(@QueryParam("username") final String userName) {
		User user = userService.findByUserName(userName);
		if (null != user) {
			return Response.ok(user).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("firstname")
	public Response searchUserByFirstName(@QueryParam("firstname") final String firstName) {
		ArrayList<User> user = (ArrayList<User>) userService.findByFirstname(firstName);

		if (null != user | !user.isEmpty()) {
			return Response.ok(user).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("lastname")
	public Response searchUserByLastName(@QueryParam("lastname") final String lastName) {
		ArrayList<User> user = (ArrayList<User>) userService.findByLastname(lastName);
		if (null != user | !user.isEmpty()) {
			return Response.ok(user).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@DELETE
	@Path("{userId}")
	public final Response deleteUser(@PathParam("userId") final String userId) {

		User user = userService.findUser(userId);
		if (user != null) {
			userService.deleteUser(user.getId());
			return Response.ok(user).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}

	}

	@PUT
	@Path("{userId}")
	public Response updateUser(@PathParam("userId") final String userId, User user) {
		user.setUserId(userId);
		user = userService.updateUser(user);

		if (user != null) {
			return Response.ok(user).build();
		} else {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@PUT
	@Path("{userId}/workitems")
	public Response addWorkItemToUser(@PathParam("userId") final String userId, final String json) {
		User user = userService.findUser(userId);

		JsonObject jobj = new Gson().fromJson(json, JsonObject.class);

		Long id = jobj.get("id").getAsLong();
		WorkItem workItem = workItemService.findWorkItem(id);

		if (null != user && null != workItem) {
			
			ArrayList<User> allUsers = userService.getAllUsers();
			for(User oneUser: allUsers){
				if(oneUser.hasWorkItem(workItem)){
					oneUser.removeWorkItem(workItem);
					userService.updateUser(oneUser);
					break;
				}
			}
			
			user.addWorkItem(workItem);
			user = userService.updateUser(user);
			return Response.ok(user).build();
		} else {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	

	@DELETE
	@Path("{userId}/workitem/{itemId}")
	public Response deleteWorkItemFromUser(@PathParam("userId") final String userId, @PathParam("itemId") final long itemId) {
		User user = userService.findUser(userId);

		WorkItem workItem = workItemService.findWorkItem(itemId);

		if (null != user && null != workItem && user.hasWorkItem(workItem)) {
			user.removeWorkItem(workItem);
			user = userService.updateUser(user);
			return Response.ok(workItem).build();
		} else {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	

	@GET
	@Path("{userId}/workitems")
	public Response getAllWorkItemsFromUser(@PathParam("userId") final String userId) {
		User user = userService.findUser(userId);

		if (null != user) {
			ArrayList<WorkItem> workItems = new ArrayList<>(user.getWorkItems());
			GenericEntity<List<WorkItem>> entity = new GenericEntity<List<WorkItem>>(workItems){
			};
			return Response.ok(entity).build();
		} else {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	public Response findAllUsers(@DefaultValue("0") @QueryParam("page") final int page,
			@DefaultValue("0") @QueryParam("size") final int size) {
		ArrayList<User> users = new ArrayList<>();
		if (page >= 0 && size > 0) {
			users = userService.getAllUsers(page, size);
		} else {
			users = userService.getAllUsers();
		}

		GenericEntity<List<User>> entity = new GenericEntity<List<User>>(users) {
		};
		return Response.ok(entity).build();
	}

}
