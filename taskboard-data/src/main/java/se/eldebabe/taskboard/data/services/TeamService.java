package se.eldebabe.taskboard.data.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import se.eldebabe.taskboard.data.models.Team;
import se.eldebabe.taskboard.data.models.User;
import se.eldebabe.taskboard.data.models.WorkItem;
import se.eldebabe.taskboard.data.repositories.TeamRepository;

public class TeamService {

	@Autowired
	private TeamRepository teamRepository;

	@Transactional
	public Team saveTeam(Team team) {
		return teamRepository.save(team);
	}

	public List<Team> deleteByName(String name) {
		return teamRepository.deleteByName(name);
	}

	public Team delete(Long id) {
		Team team = teamRepository.findOne(id);

		if (team != null) {
			return teamRepository.deleteById(id).get(0);
		}
		return null;
	}

	public Team findTeamByName(String name) {
		List<Team> teams = teamRepository.findByName(name);
		if (teams.size() != 0) {
			return teamRepository.findByName(name).get(0);
		} else {
			return null;
		}
	}

	@Transactional
	public Team updateTeam(Team team) {
		return teamRepository.save(team);
	}

	public Team findById(Long id) {
		return teamRepository.findOne(id);
	}

	public List<Team> findAllTeams() {
		return (List<Team>) teamRepository.findAll();
	}

	@Transactional
	public Team addUserToTeam(User user, Team team) {
		team.addUser(user);
		return updateTeam(team);
	}

	public Collection<User> findUsersInTeam(Long id) {
		return teamRepository.findOne(id).getUsers();
	}

	public Collection<WorkItem> findWorkItemsInTeam(Long id) {
		Collection<User> users = findUsersInTeam(id);
		Collection<WorkItem> workItems = new HashSet<>();
		for (User user : users) {
			for (WorkItem workItem : user.getWorkItems()) {
				workItems.add(workItem);
			}
		}
		return workItems;
	}

}