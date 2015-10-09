package se.eldebabe.taskboard.data.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import se.eldebabe.taskboard.data.models.Team;

public interface TeamRepository extends CrudRepository<Team, Long> {

	List<Team> findByName(String name);

	@Transactional
	List<Team> deleteByName(String name);

	@Transactional
	List<Team> deleteById(Long id);
}