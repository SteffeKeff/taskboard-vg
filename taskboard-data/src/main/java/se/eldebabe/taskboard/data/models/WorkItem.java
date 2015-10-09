package se.eldebabe.taskboard.data.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "work_items")
public class WorkItem extends AbstractEntity {

	@Column(name = "title")
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;

	@LastModifiedDate
	@Column(name = "date")
	private Long date;

	public Long getLastModifiedDate() {
		return date;
	}

	//cascade = CascadeType.ALL
	@OneToOne(cascade= CascadeType.REMOVE,fetch = FetchType.EAGER)
	private Issue issue;

	protected WorkItem() {
	}

	public WorkItem(String title, String description) {
		status = Status.NOT_STARTED;
		this.title = title;
		this.description = description;
	}

	public WorkItem(Long id, String title, String description, Status status) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.status = status;
	}

	public WorkItem(Long id, String title, String description, Status status, Issue issue) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.status = status;
		this.issue = issue;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Status getStatus() {
		return status;
	}

	public Issue getIssue() {
		return issue;
	}

	public void setCompleted(Status status) {
		this.status = status;
	}

	public void setIssue(Issue issue) {
		this.issue = issue;
	}
}
