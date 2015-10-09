package se.eldebabe.taskboard.data.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import se.eldebabe.taskboard.data.models.Status;
import se.eldebabe.taskboard.data.models.WorkItem;

public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {

	List<WorkItem> findByStatus(Status status);

	List<WorkItem> findByDescriptionContaining(String description);

	List<WorkItem> findByIssueIdNotNull();

	ArrayList<WorkItem> findByDateBetweenAndStatus(Long dateFrom, Long dateTo, Status status);

}
