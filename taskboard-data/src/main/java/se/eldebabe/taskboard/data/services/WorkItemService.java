package se.eldebabe.taskboard.data.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import se.eldebabe.taskboard.data.models.Status;
import se.eldebabe.taskboard.data.models.WorkItem;
import se.eldebabe.taskboard.data.repositories.WorkItemRepository;

public class WorkItemService {

	@Autowired
	private WorkItemRepository workItemRepository;

	public WorkItem saveWorkItem(WorkItem workItem) {
		return workItemRepository.save(workItem);
	}

	public WorkItem findWorkItem(long id) {
		return workItemRepository.findOne(id);
	}

	public void deleteWorkItem(long id) {
		workItemRepository.delete(id);
	}

	public List<WorkItem> findWorkItemsWithStatus(Status status) {
		return workItemRepository.findByStatus(status);
	}

	public List<WorkItem> findWorkItemWithDescriptionContaining(String description) {
		return workItemRepository.findByDescriptionContaining(description);
	}

	public List<WorkItem> findWorkItemsWithIssue() {
		return workItemRepository.findByIssueIdNotNull();
	}

	public ArrayList<WorkItem> findCompletedWorkItemsWithinDate(Date dateFrom, Date dateTo) {
		return workItemRepository.findByDateBetweenAndStatus(dateFrom.getTime(), dateTo.getTime(), Status.COMPLETED);
	}

	public ArrayList<WorkItem> getAllWorkItems(int page, int size) {
		PageRequest request = new PageRequest(page, size);

		Page<WorkItem> dbWorkItems = workItemRepository.findAll(request);
		ArrayList<WorkItem> workItems = new ArrayList<>();
		for (WorkItem workItem : dbWorkItems) {
			workItems.add(workItem);
		}
		return workItems;
	}

	public ArrayList<WorkItem> getAllWorkItems() {

		Iterable<WorkItem> dbWorkItems = workItemRepository.findAll();
		ArrayList<WorkItem> workItems = new ArrayList<>();
		for (WorkItem workItem : dbWorkItems) {
			workItems.add(workItem);
		}
		return workItems;
	}

}
