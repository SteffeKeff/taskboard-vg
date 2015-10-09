package se.eldebabe.taskboard.data.services;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import se.eldebabe.taskboard.data.models.Issue;
import se.eldebabe.taskboard.data.models.Status;
import se.eldebabe.taskboard.data.models.WorkItem;

public final class WorkItemServiceTest {

	private static AnnotationConfigApplicationContext context;
	private static WorkItemService workItemService;
	private WorkItem workItem;

	@BeforeClass
	public static void setupConfigs() {
		context = new AnnotationConfigApplicationContext();
		context.scan("se.eldebabe.taskboard.data.configs");
		context.refresh();
		workItemService = context.getBean(WorkItemService.class);
	}

	@Test
	public void assertThatWorkItemIsSavable() {
		workItem = new WorkItem("Skapa hemsida1111", "Lite html, lite css, gärna mycket javascript!");
		assertThat("Added WorkItem should be returned", workItem, is(workItemService.saveWorkItem(workItem)));
	}

	@Test
	public void assertThatWorkItemStatusCanBeChanged() {
		workItem = new WorkItem("Skapa hemsida2222", "Lite html, lite css, gärna mycket javascript!");
		workItem = workItemService.saveWorkItem(workItem);
		workItem.setCompleted(se.eldebabe.taskboard.data.models.Status.IN_PROGRESS);
		workItemService.saveWorkItem(workItem);
		assertThat("WorkItem status should be true", se.eldebabe.taskboard.data.models.Status.IN_PROGRESS,
				is(workItemService.findWorkItem(workItem.getId()).getStatus()));
	}

	@Test
	public void assertThatWorkItemCanBeDeleted() {
		workItem = new WorkItem("Skapa hemsida3333", "Lite html, lite css, gärna mycket javascript!");
		workItemService.saveWorkItem(workItem);
		workItemService.deleteWorkItem(workItem.getId());
		assertThat("workItemService cant find work item", null, is(workItemService.findWorkItem(workItem.getId())));
	}

	@Test
	public void assertThatWorkItemsCanBeFetchedByStatus() {
		WorkItem i1 = new WorkItem("hej1", "hohoho");
		WorkItem i2 = new WorkItem("hej2", "hehoho");
		WorkItem i3 = new WorkItem("hej3", "hahoho");
		i1.setCompleted(Status.COMPLETED);
		i2.setCompleted(Status.COMPLETED);
		i3.setCompleted(Status.COMPLETED);
		workItemService.saveWorkItem(i1);
		workItemService.saveWorkItem(i2);
		workItemService.saveWorkItem(i3);
		assertThat("There should be 3 work items with status.completed", 3,
				is(workItemService.findWorkItemsWithStatus(se.eldebabe.taskboard.data.models.Status.COMPLETED).size()));
	}

	@Test
	public void assertThatWorkItemsCanBeFetchedByDescription() {
		WorkItem i1 = new WorkItem("theWorkItem", "can you find this workitem");
		workItemService.saveWorkItem(i1);
		assertThat("There should be one work item containing description 'this workitem'", 1,
				is(workItemService.findWorkItemWithDescriptionContaining("this workitem").size()));
	}

	/* //// These test have relations //// */

	@Test
	public void assertThatWorkItemCanHaveAnIssue() {
		workItem = new WorkItem("Skapa hemsida2123", "Lite html, lite css, gärna mycket javascript!");
		Issue issue = new Issue("Jaha ja detta var ju ett issue då va");
		workItem.setIssue(issue);
		workItemService.saveWorkItem(workItem);
		assertThat("work item from db should have same issue as workItem", null,
				not(workItemService.findWorkItem(workItem.getId()).getIssue()));
	}

	@Test
	public void assertThatAllWorkItemWhichHasAnIssueCanBeFetched() {
		workItem = new WorkItem("Skapa hemsidaaaaaaannananan", "Lite html, lite css, gärna mycket javascript!!!wiii!");
		WorkItem workItem2 = new WorkItem("Skapa hemsidaaaaaa", "Lite html, lite css, gärna mycket javascript!");
		Issue issue = new Issue("Jaha ja detta var ju ett issue då va IGEN IGEN IGEN");
		Issue issue2 = new Issue("Jaha ja detta var ju ett issue då va IGEN IGEN IGEN ooooooo");
		workItem.setIssue(issue);
		workItem2.setIssue(issue2);
		workItemService.saveWorkItem(workItem);
		workItemService.saveWorkItem(workItem2);
		assertThat("should have two work items with issuies", 2, is(workItemService.findWorkItemsWithIssue().size()));
	}

	@AfterClass
	public static void tearDown() {
		context.close();
	}

}
