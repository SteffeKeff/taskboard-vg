package se.eldebabe.taskboard.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import se.eldebabe.taskboard.data.models.Issue;

public interface IssueRepository extends JpaRepository<Issue, Long> {

}
