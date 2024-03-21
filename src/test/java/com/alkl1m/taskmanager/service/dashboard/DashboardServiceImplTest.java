package com.alkl1m.taskmanager.service.dashboard;

import com.alkl1m.taskmanager.controller.payload.dashboard.DashboardDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.TaskRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private DashboardServiceImpl dashboardService;
    private User user;
    private Project project;
    private List<Project> projects;
    private Task task1;
    private Task task2;
    private List<Task> tasks;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .password("123")
                .role(Role.USER)
                .enabled(false)
                .build();
        project = Project.builder()
                .id(1L)
                .name("test1 project")
                .description("test1 project description")
                .status(Status.IN_WORK)
                .createdAt(Instant.now())
                .user(user)
                .build();
        task1 = Task.builder()
                .id(1L)
                .name("test1 task")
                .description("test1 task description")
                .status(Status.IN_WORK)
                .createdAt(Instant.now())
                .user(user)
                .build();
        task2 = Task.builder()
                .id(2L)
                .name("test1 task")
                .description("test1 task description")
                .status(Status.IN_WORK)
                .createdAt(Instant.now())
                .user(user)
                .build();
        projects = Arrays.asList(project);
        tasks = Arrays.asList(task1, task2);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        projectRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    public void shouldReturnDashboardDtoThatContainsAllProjectsAndTasks_WhenUserIsValid() {
        when(projectRepository.findByUserIdOrderByCreatedAtDesc(user.getId())).thenReturn(projects);
        when(taskRepository.findTop50ByUserIdOrderByDeadlineAsc(user.getId())).thenReturn(tasks);
        DashboardDto dashboardDto = dashboardService.getDashboardData(user.getId());
        assertEquals(1, dashboardDto.projects().size());
        assertEquals(2, dashboardDto.tasks().size());
    }

}