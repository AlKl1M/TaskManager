package com.alkl1m.taskmanager.service.project;

import com.alkl1m.taskmanager.dto.project.CreateProjectCommand;
import com.alkl1m.taskmanager.dto.project.ProjectDto;
import com.alkl1m.taskmanager.dto.project.UpdateProjectCommand;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.exception.ProjectNotFoundException;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectServiceImplTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ProjectServiceImpl projectService;
    private User user;
    private Project project;
    private CreateProjectCommand createProjectCommand;

    @BeforeEach
    void setUp() {
        user = User.builder().name("user").email("user@mail.com").password("123").build();
        project = Project.builder().name("project").description("project").createdAt(Instant.now()).status(Status.IN_WORK).user(user).build();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    public void ProjectService_getAll_Successful() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        List<Project> projects = new ArrayList<>();
        projects.add(project);
        projects.add(project);
        projects.add(project);
        Mockito.when(projectRepository.findAllByUserId(user.getId())).thenReturn(projects);
        List<ProjectDto> result = projectService.getAllProjects(user.getId());
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    public void ProjectService_create_Successful() {
        createProjectCommand = new CreateProjectCommand(user.getId(), "project", "project");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepository.save(Mockito.any(Project.class))).thenReturn(project);

        ProjectDto savedProject = projectService.create(createProjectCommand);
        assertNotNull(savedProject);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void ProjectService_create_ExceptionThrown() {
        createProjectCommand = new CreateProjectCommand(user.getId(), "project", "project");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.countUserProjects(user.getId())).thenReturn(20);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> projectService.create(createProjectCommand));
        assertEquals("User has reached the maximum number of projects.", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void ProjectService_create_ReturnNull() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        createProjectCommand = new CreateProjectCommand(user.getId(), "project", "project");
        ProjectDto result = projectService.create(createProjectCommand);
        assertNull(result);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void ProjectService_update_Successful() {
        UpdateProjectCommand command = new UpdateProjectCommand(project.getId(), "Updated", "Updated Description");
        when(projectRepository.findById(command.id())).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        ProjectDto result = projectService.update(command);
        assertEquals(command.name(), result.name());
        assertEquals(command.description(), result.description());
        verify(projectRepository, times(1)).findById(command.id());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void ProjectService_update_ThrowsException() {
        UpdateProjectCommand command = new UpdateProjectCommand(project.getId(), "Updated", "Updated Description");
        when(projectRepository.findById(command.id())).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> projectService.update(command));
        verify(projectRepository, times(1)).findById(command.id());
    }

    @Test
    public void ProjectService_delete_Successful() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        projectService.delete(project.getId());
        verify(projectRepository, times(1)).findById(project.getId());
        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    public void ProjectService_delete_ThrowsException() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> projectService.delete(project.getId()));
        verify(projectRepository, times(1)).findById(project.getId());
        verify(projectRepository, never()).delete(any(Project.class));
    }

    @Test
    public void ProjectService_changeStatus_Successful() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        projectService.changeStatus(project.getId());
        verify(projectRepository, times(1)).findById(project.getId());
        verify(projectRepository, times(1)).save(project);
        assertEquals(Status.DONE, project.getStatus());
    }

    @Test
    public void ProjectService_changeStatus_ThrowsException() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> projectService.changeStatus(project.getId()));
        verify(projectRepository, times(1)).findById(project.getId());
        verify(projectRepository, never()).save(any(Project.class));
    }
}