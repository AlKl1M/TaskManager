package com.alkl1m.taskmanager.service.project;

import com.alkl1m.taskmanager.dto.project.CreateProjectCommand;
import com.alkl1m.taskmanager.dto.project.ProjectDto;
import com.alkl1m.taskmanager.dto.project.UpdateProjectCommand;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.repository.ProjectRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ProjectServiceImpl projectService;
    private User user;
    private Project project1;
    private Project project2;
    private CreateProjectCommand cmd;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .password("123")
                .role(Role.USER)
                .enabled(true)
                .build();
        project1 = Project.builder()
                .name("test1 project")
                .description("test1 project description")
                .status(Status.IN_WORK)
                .createdAt(Instant.now())
                .user(user)
                .build();
        project2 = Project.builder()
                .name("test2 project")
                .description("test2 project description")
                .status(Status.DONE)
                .createdAt(Instant.now())
                .user(user)
                .build();
        cmd = CreateProjectCommand.builder()
                .id(1L)
                .name("test1 project")
                .description("test1 project description")
                .build();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    public void shouldReturnAllProjects_WithValidProject() {
        List<Project> projects = Arrays.asList(project1, project2);
        when(projectRepository.findAllByUserId(user.getId())).thenReturn(projects);
        List<ProjectDto> result = projectService.getAllProjects(user.getId());
        assertEquals(2, result.size());
        assertEquals(project1.getId(), result.get(0).id());
        assertEquals(project1.getName(), result.get(0).name());
        assertEquals(project2.getId(), result.get(1).id());
        assertEquals(project2.getName(), result.get(1).name());
    }

    @Test
    public void shouldReturnProjectDtoList_WithQuery() {
        String query = "test";
        List<Project> projects = Arrays.asList(project1, project2);
        when(projectRepository.findByQueryAndUserId(query, user.getId())).thenReturn(projects);
        List<ProjectDto> expected = projects.stream()
                .map(ProjectDto::from)
                .collect(Collectors.toList());
        List<ProjectDto> result = projectService.getAllProjectsByQuery(user.getId(), query);
        verify(projectRepository).findByQueryAndUserId(query, user.getId());
        assertEquals(expected, result);
    }

    @Test
    public void shouldCreateNewProject_WhenCmdIsValidAndUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.countUserProjects(user.getId())).thenReturn(10);
        when(projectRepository.save(any(Project.class))).thenReturn(project1);
        ProjectDto result = projectService.create(cmd);
        assertNotNull(result);
        assertEquals(project1.getId(), result.id());
        assertEquals(project1.getName(), result.name());
        assertEquals(project1.getDescription(), result.description());
        assertEquals(project1.getStatus(), result.status());
    }

    @Test
    public void shouldUpdateProject_WhenProjectExists() {
        UpdateProjectCommand cmd = new UpdateProjectCommand(1L,
                "Updated Project",
                "Updated Description");
        when(projectRepository.findById(cmd.id())).thenReturn(Optional.of(project1));
        when(projectRepository.save(any(Project.class))).thenReturn(project1);
        projectService.update(cmd);
        assertEquals(project1.getName(), cmd.name());
        assertEquals(project1.getDescription(), cmd.description());
    }

    @Test
    public void shouldDeleteProject_WhenProjectExists() {
        when(projectRepository.findById(project1.getId())).thenReturn(Optional.of(project1));
        projectService.delete(project1.getId());
        verify(projectRepository).delete(project1);
    }

    @Test
    public void shouldChangeProjectStatus_WhenProjectStatusIsInWork() {
        when(projectRepository.findById(project1.getId())).thenReturn(Optional.of(project1));
        projectService.changeStatus(project1.getId());
        assertEquals(Status.DONE, project1.getStatus());
        assertNotNull(project1.getDoneAt());
        verify(projectRepository).save(project1);
    }

    @Test
    public void shouldThrowException_WhenUserReachedMaxNumOfProjects() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.countUserProjects(user.getId())).thenReturn(20);
        IllegalStateException exception = assertThrows(IllegalStateException.class, ()-> {
            projectService.create(cmd);
        });
        assertEquals("User has reached the maximum number of projects.", exception.getMessage());
    }

    @Test
    public void shouldReturnNull_WhenUserDoesNotExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        ProjectDto result = projectService.create(cmd);
        assertNull(result);
        verify(userRepository).findById(user.getId());
    }

    @Test
    public void shouldChangeStatus_WhenProjectStatusDone() {
        when(projectRepository.findById(project2.getId())).thenReturn(Optional.of(project2));
        projectService.changeStatus(project2.getId());
        assertEquals(Status.IN_WORK, project2.getStatus());
        verify(projectRepository).save(project2);
    }
}