package se.eldebabe.taskboard.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "issues")
public class Issue extends AbstractEntity {

	@Column
	private String description;

	protected Issue() {
	}

	public Issue(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
