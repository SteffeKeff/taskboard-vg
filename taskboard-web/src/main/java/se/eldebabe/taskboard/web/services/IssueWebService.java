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

import se.eldebabe.taskboard.data.models.Issue;
import se.eldebabe.taskboard.data.services.IssueService;

@Path("issues")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class IssueWebService {

	private static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	private static IssueService issueService;

	static {
		context.scan("se.eldebabe.taskboard.data.configs");
		context.refresh();
		issueService = context.getBean(IssueService.class);
	}

	@Context
	public UriInfo uriInfo;

	@POST
	public final Response saveIssue(Issue issue) {

		issue = issueService.saveIssue(issue);
		if (null != issue) {
			final URI location = uriInfo.getAbsolutePathBuilder().path(issue.getId().toString()).build();
			return Response.created(location).build();
		} else {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path("{issueId}")
	public final Response getIssue(@PathParam("issueId") final Long id) {

		Issue issue = issueService.findIssueById(id);
		if (null != issue) {
			return Response.ok(issue).build();
		} else {
			return Response.noContent().build();
		}
	}

	@DELETE
	@Path("{issueId}")
	public final Response deleteIssue(@PathParam("issueId") final Long id) {
		Issue issue = issueService.findIssueById(id);
		issue = issueService.deleteIssue(id);
		if (null != issue) {
			return Response.ok(issue).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@PUT
	@Path("{issueId}")
	public final Response updateIssue(@PathParam("issueId") final Long id, Issue issue) {

		issue = issueService.updateIssue(issue);
		if (null != issue) {
			return Response.ok(issue).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@GET
	public Response findAllIssues(@DefaultValue("0") @QueryParam("page") final int page,
			@DefaultValue("0") @QueryParam("size") final int size) {
		ArrayList<Issue> issues = new ArrayList<>();
		if (page >= 0 && size > 0) {
			issues = issueService.getAllIssues(page, size);
		} else {
			issues = issueService.getAllIssues();
		}
		GenericEntity<List<Issue>> entity = new GenericEntity<List<Issue>>(issues){
		};
		return Response.ok(entity).build();
	}

}
