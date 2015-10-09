package se.eldebabe.taskboard.data.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import se.eldebabe.taskboard.data.models.Issue;

public final class IssueServiceTest {

	private static AnnotationConfigApplicationContext context;
	private static IssueService issueService;
	private Issue issue;

	@BeforeClass
	public static void setupConfigs() {
		context = new AnnotationConfigApplicationContext();
		context.scan("se.eldebabe.taskboard.data.configs");
		context.refresh();
		issueService = context.getBean(IssueService.class);
	}

	@Test
	public void assertThatIssueIsSavable() {
		issue = new Issue("Förlängd leveranstid till 6/9-15");
		assertThat("Added Issue should be returned", issue, is(issueService.saveIssue(issue)));
	}

	@Test
	public void assertThatIssueIsGettable() {
		issue = new Issue("Du glömde pusha css");
		issueService.saveIssue(issue);
		assertThat("issueService should find issue", 1L, is(issueService.findIssueById(1L).getId()));
	}

	@Test
	public void assertThatIssueCanBeUpdated() {
		issue = new Issue("Använd hellre JQuery än vanlig Javascript");
		issue = issueService.saveIssue(issue);
		issue.setDescription("Förlängd leveranstid till 18/9-15");
		issueService.saveIssue(issue);
		assertThat("", issue.getDescription(), is(issueService.findIssueById(1L).getDescription()));
	}

	@Test
	public void assertThatIssueCanBeDeleted() {
		issue = new Issue("Problem med databasen");
		issueService.saveIssue(issue);
		issueService.deleteIssue(issue.getId());
		assertThat("issueService must not find issue", null, is(issueService.findIssueById(issue.getId())));
	}

	@AfterClass
	public static void tearDown() {
		context.close();
	}

}
