package se.eldebabe.taskboard.web.services;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import se.eldebabe.taskboard.data.models.Issue;
import se.eldebabe.taskboard.data.models.User;
import se.eldebabe.taskboard.data.models.WorkItem;
import se.eldebabe.taskboard.data.services.IssueService;
import se.eldebabe.taskboard.data.services.UserService;
import se.eldebabe.taskboard.data.services.WorkItemService;

@Path("workitems")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class WorkItemWebService {

	private static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	private static WorkItemService workItemService = new WorkItemService();
	private static IssueService issueService = new IssueService();
	private static UserService userService;

	static {
		context.scan("se.eldebabe.taskboard.data.configs");
		context.refresh();
		workItemService = context.getBean(WorkItemService.class);
		issueService = context.getBean(IssueService.class);
		userService = context.getBean(UserService.class);
	}

	@Context
	public UriInfo uriInfo;

	@POST
	public final Response saveWorkItem(WorkItem workItem) {

		if(workItem.getIssue() != null){
			issueService.saveIssue(workItem.getIssue());	
		}

		workItem = workItemService.saveWorkItem(workItem);
		if (null != workItem) {
			final URI location = uriInfo.getAbsolutePathBuilder().path(workItem.getId().toString()).build();
			return Response.created(location).build();
		} else {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path("issue")
	public final Response getWorkItemsWithIssues(@QueryParam("issue") final boolean hasIssue) {

		if (hasIssue) {
			ArrayList<WorkItem> workItems = (ArrayList<WorkItem>) workItemService.findWorkItemsWithIssue();
			if (!workItems.isEmpty()) {
				GenericEntity<List<WorkItem>> entity = new GenericEntity<List<WorkItem>>(workItems) {
				};
				return Response.ok(entity).build();
			}
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@GET
	@Path("description")
	public final Response searchForWorkItemsWithDesc(@QueryParam("description") final String description) {

		ArrayList<WorkItem> workItems = (ArrayList<WorkItem>) workItemService
				.findWorkItemWithDescriptionContaining(description);

		if (!workItems.isEmpty()) {
			GenericEntity<List<WorkItem>> entity = new GenericEntity<List<WorkItem>>(workItems) {
			};
			return Response.ok(entity).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@PUT
	@Path("{workItemId}/issue")
	public final Response updateIssue(@PathParam("workItemId") final Long id, final Issue issue) {

		WorkItem workItem = workItemService.findWorkItem(id);
		workItem.setIssue(issue);

		workItem = workItemService.saveWorkItem(workItem);
		if (null != issue) {
			return Response.ok(workItem).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@PUT
	@Path("{workItemId}")
	public final Response updateWorkItem(@PathParam("workItemId") final Long id, final WorkItem newWorkItem) {
		
		WorkItem workItem = workItemService.findWorkItem(newWorkItem.getId());
		if (workItem != null) {

			if (newWorkItem.getIssue() != null) {
				if(newWorkItem.getIssue().getId() == null){
					issueService.saveIssue(newWorkItem.getIssue());
				}else{
					issueService.updateIssue(newWorkItem.getIssue());
				}
			}
			
			workItem = workItemService.saveWorkItem(newWorkItem);

			return Response.ok(workItem).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@PUT
	@Path("{workItemId}/status")
	public final Response changeStatus(@PathParam("workItemId") final Long id, final String json) {
		se.eldebabe.taskboard.data.models.Status status;

		JsonObject jobj = new Gson().fromJson(json, JsonObject.class);

		status = se.eldebabe.taskboard.data.models.Status.valueOf(jobj.get("status").getAsString().toUpperCase());

		if (null != status) {
			WorkItem workItem = workItemService.findWorkItem(id);
			if (null != workItem) {
				workItem.setCompleted(status);

				workItem = workItemService.saveWorkItem(workItem);
				return Response.ok(workItem).build();
			} else {
				return Response.status(Status.NOT_FOUND).build();
			}
		}
		return Response.status(Status.EXPECTATION_FAILED).build();
	}

	@DELETE
	@Path("{workItemId}")
	public final Response deleteWorkItem(@PathParam("workItemId") final Long id) {
		WorkItem workItem = workItemService.findWorkItem(id);
		if (null != workItem) {
			ArrayList<User> allUsers = userService.getAllUsers();
			for(User oneUser: allUsers){
				if(oneUser.hasWorkItem(workItem)){
					oneUser.removeWorkItem(workItem);
					userService.updateUser(oneUser);
					break;
				}
			}
			workItemService.deleteWorkItem(id);
			return Response.ok(workItem).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("/status/{workItemStatus}")
	public final Response getWorkitemWithStatus(@PathParam("workItemStatus") final String workItemStatus, @Context SecurityContext securityContext) {

		se.eldebabe.taskboard.web.filters.AuthenticationFilter.User user = (se.eldebabe.taskboard.web.filters.AuthenticationFilter.User)securityContext.getUserPrincipal();
        
        if(user != null){
        	String role = user.getName();
        	if(role.equals("user")){
        		List<WorkItem> workItems = null;
        		se.eldebabe.taskboard.data.models.Status status = null;

        		try {
        			status = se.eldebabe.taskboard.data.models.Status.valueOf(workItemStatus.toUpperCase());
        		} catch (RuntimeException IllegalArgumentException) {
        			return Response.status(406).build();
        		}

        		workItems = workItemService.findWorkItemsWithStatus(status);
        		if (null != workItems) {
        			GenericEntity<List<WorkItem>> entity = new GenericEntity<List<WorkItem>>(workItems) {
        			};
        			return Response.ok(entity).build();
        		} else {
        			return Response.noContent().build();
        		}
        	}
        }
        
        return Response.status(Status.UNAUTHORIZED).build();
	}

	@GET
	@Path("/completed")
	public final Response getCompletedWorkItemsWithinDate(
			@DefaultValue("") @QueryParam("fromDate") final String fromDate,
			@DefaultValue("") @QueryParam("toDate") final String toDate) {
		if (fromDate.length() == 8 && toDate.length() == 8) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date dateFrom, dateTo;
			try {
				dateFrom = sdf.parse(fromDate);
				dateTo = sdf.parse(toDate);

				ArrayList<WorkItem> workItems = workItemService.findCompletedWorkItemsWithinDate(dateFrom, dateTo);

				GenericEntity<List<WorkItem>> entity = new GenericEntity<List<WorkItem>>(workItems) {
				};
				return Response.ok(entity).build();
			} catch (ParseException e) {
				return Response.status(Status.BAD_REQUEST).build();
			}

		}
		return Response.status(Status.BAD_REQUEST).build();

	}

	@GET
	public Response findAllWorkItems(@DefaultValue("0") @QueryParam("page") final int page,
			@DefaultValue("0") @QueryParam("size") final int size)
					throws JsonGenerationException, JsonMappingException, IOException {
		ArrayList<WorkItem> workItems = new ArrayList<>();
		if (page >= 0 && size > 0) {
			workItems = workItemService.getAllWorkItems(page, size);
		} else {
			workItems = workItemService.getAllWorkItems();
		}

		GenericEntity<List<WorkItem>> entity = new GenericEntity<List<WorkItem>>(workItems) {
		};
		return Response.ok(entity).build();
	}

}
