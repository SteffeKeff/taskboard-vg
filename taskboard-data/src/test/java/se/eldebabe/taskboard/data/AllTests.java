package se.eldebabe.taskboard.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import se.eldebabe.taskboard.data.services.*;

@RunWith(Suite.class)
@SuiteClasses({ IssueServiceTest.class, TeamServiceTest.class, UserServiceTest.class, WorkItemServiceTest.class })
public class AllTests {

}
