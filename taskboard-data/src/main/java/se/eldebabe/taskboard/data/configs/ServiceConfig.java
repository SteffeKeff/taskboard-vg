package se.eldebabe.taskboard.data.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import se.eldebabe.taskboard.data.services.IssueService;
import se.eldebabe.taskboard.data.services.TeamService;
import se.eldebabe.taskboard.data.services.UserService;
import se.eldebabe.taskboard.data.services.WorkItemService;

@Configuration
public class ServiceConfig {

	@Bean
	public UserService userService() {
		return new UserService();
	}

	@Bean
	public TeamService teamService() {
		return new TeamService();
	}

	@Bean
	public WorkItemService workItemService() {
		return new WorkItemService();
	}

	@Bean
	public IssueService issueService() {
		return new IssueService();
	}

}
