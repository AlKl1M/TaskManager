package com.alkl1m.taskmanager.service.task;

import com.alkl1m.taskmanager.controller.payload.project.CreateProjectCommand;
import com.alkl1m.taskmanager.controller.payload.project.ProjectDto;
import com.alkl1m.taskmanager.controller.payload.task.CreateBackTaskDto;
import com.alkl1m.taskmanager.controller.payload.task.CreateTaskCommand;
import com.alkl1m.taskmanager.controller.payload.task.TaskDto;
import com.alkl1m.taskmanager.controller.payload.task.UpdateTaskCommand;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.TaskRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import com.alkl1m.taskmanager.service.project.ProjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTests {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskServiceImpl taskService;
    @InjectMocks
    private ProjectServiceImpl projectService;
    private Task task1;
    private Task task2;
    private Project project;
    private CreateTaskCommand createTaskCommand;
    private CreateProjectCommand createProjectCommand;

    private User user;

    @BeforeEach
    public void Setup(){
        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .password("123")
                .role(Role.USER)
                .enabled(true)
                .build();
        task1 = Task.builder()
                .id(1L)
                .name("Apple")
                .description("TestTask1Description")
                .createdAt(Instant.now())
                .doneAt(null)
                .deadline(Instant.MAX)
                .status(Status.IN_WORK)
                .tags("tag1")
                .build();

        task2 = Task.builder()
                .id(2L)
                .name("Home")
                .description("TestTask2Description")
                .createdAt(Instant.now())
                .doneAt(null)
                .deadline(Instant.MAX)
                .status(Status.DONE)
                .tags("tag2")
                .build();

        project = Project.builder()
                .id(1L)
                .name("TestProject1")
                .description("TestProjectDescription")
                .createdAt(Instant.now())
                .doneAt(Instant.MAX)
                .status(Status.IN_WORK)
                .user(user)
                .build();

    }
    @AfterEach
    public void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    public void TaskService_CreateTaskIsValidTagsAndProjectIsPresent_ReturnsTaskDto() {
        createTaskCommand = CreateTaskCommand.builder()
                .id(1L)
                .name("TestTask1")
                .description("TestDescription")
                .deadline(Instant.MAX)
                .tags(List.of("tag1", "tag2", "tag3"))
                .build();
        createProjectCommand = CreateProjectCommand.builder()
                .id(1L)
                .name("test1 project")
                .description("test1 project description")
                .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        ProjectDto projectSaved = projectService.create(createProjectCommand);
        TaskDto taskSavedResult = taskService.create(createTaskCommand, projectSaved.id());

        assertNotNull(taskSavedResult);
        assertEquals(task1.getId(), taskSavedResult.id());
        assertEquals(task1.getName(), taskSavedResult.name());
        assertEquals(task1.getDescription(), taskSavedResult.description());
        assertEquals(task1.getStatus(), taskSavedResult.status());
        assertEquals(task1.getDeadline(), taskSavedResult.deadline());
        assertEquals(task1.getTags(), taskSavedResult.tags());
    }

    @Test
    public void TaskService_CreateTaskNoSizeValidTags_ReturnsTaskDto() {
        createTaskCommand = CreateTaskCommand.builder()
                .id(1L)
                .name("TestTask1")
                .description("TestDescription")
                .deadline(Instant.MAX)
                .tags(List.of("tag1", "tag2", "tag3", "tag4"))
                .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class, ()-> {
            taskService.create(createTaskCommand, project.getId());
        });
        assertEquals("Tags size should be > 2 and < 20, maxTags = 3", exception.getMessage());
    }
    @Test
    public void TaskService_CreateTaskNoSizeValidTag_ReturnsTaskDto() {
        createTaskCommand = CreateTaskCommand.builder()
                .id(1L)
                .name("TestTask1")
                .description("TestDescription")
                .deadline(Instant.MAX)
                .tags(List.of("123456789012345678901", "t", "tag3"))
                .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class, ()-> {
            taskService.create(createTaskCommand, project.getId());
        });
        assertEquals("Tags size should be > 2 and < 20, maxTags = 3", exception.getMessage());
    }
    @Test
    public void TaskService_CreateTaskIsNotProjectPresent_ReturnsTaskDto() {
        createTaskCommand = CreateTaskCommand.builder()
                .id(1L)
                .name("TestTask1")
                .description("TestDescription")
                .deadline(Instant.MAX)
                .tags(List.of("tag1", "tag2", "tag3"))
                .build();

        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());

        TaskDto taskSavedResult = taskService.create(createTaskCommand, project.getId());

        assertNull(taskSavedResult);
        verify(projectRepository).findById(project.getId());
    }

    @Test
    public void TaskService_GetAllTasksBySearchWord_ReturnsBackTaskDto() {
        List<TaskDto> listBackTask = List.of(TaskDto.from(task1));
        String searchWord = "Apple";
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(taskRepository.getAllTasksBySearchWord(user.getId(), project, searchWord)).thenReturn(listBackTask);

        List<CreateBackTaskDto> backTaskDtoResult =
                taskService.getAllTasksBySearchWord(user.getId(), project.getId(), searchWord);

        assertEquals(1, backTaskDtoResult.size());
        assertEquals(task1.getId(), backTaskDtoResult.get(0).id());
        assertEquals(task1.getName(), backTaskDtoResult.get(0).name());
    }
    @Test
    public void TaskService_GetAllTasksByTag_ReturnsBackTaskDto() {
        List<TaskDto> listBackTask = List.of(TaskDto.from(task1));
        String tag = "tag1";
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(taskRepository.getAllTasksByTag(user.getId(), project, tag)).thenReturn(listBackTask);

        List<CreateBackTaskDto> backTaskDtoResult =
                taskService.getAllTasksByTag(user.getId(), project.getId(), tag);

        assertEquals(1, backTaskDtoResult.size());
        assertEquals(task1.getId(), backTaskDtoResult.get(0).id());
        assertEquals(task1.getName(), backTaskDtoResult.get(0).name());
    }
    @Test
    public void TaskService_GetAllTasks_ReturnsAllTasks() {
        List<TaskDto> listBackTask = Arrays.asList(TaskDto.from(task1), TaskDto.from(task2));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(taskRepository.getAllTasks(user.getId(), project)).thenReturn(listBackTask);

        List<CreateBackTaskDto> backTaskDtoResultNullSearchWord =
                taskService.getAllTasksBySearchWord(user.getId(), project.getId(), null);
        List<CreateBackTaskDto> backTaskDtoResultNullTag =
                taskService.getAllTasksByTag(user.getId(), project.getId(), null);

        assertEquals(2, backTaskDtoResultNullSearchWord.size());
        assertEquals(2, backTaskDtoResultNullTag.size());

        assertEquals(task1.getId(), backTaskDtoResultNullSearchWord.get(0).id());
        assertEquals(task2.getId(), backTaskDtoResultNullSearchWord.get(1).id());

        assertEquals(task1.getId(), backTaskDtoResultNullTag.get(0).id());
        assertEquals(task2.getId(), backTaskDtoResultNullTag.get(1).id());

    }


    @Test
    public void TaskService_UpdateTask_ReturnTaskDto(){
        UpdateTaskCommand updateTaskCommand = new UpdateTaskCommand(
                1L,
                "NewName",
                "NewDesc",
                Instant.now(),
                List.of("tag1", "tag2")
        );
        when(taskRepository.findById(updateTaskCommand.id())).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        TaskDto taskDtoResult = taskService.update(updateTaskCommand);

        assertNotNull(taskDtoResult);
        assertEquals(task1.getName(), taskDtoResult.name());
        assertEquals(task1.getDescription(), taskDtoResult.description());
    }
    @Test
    public void TaskService_DeleteTask(){
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.of(task1));

        taskService.delete(task1.getId());

        verify(taskRepository).delete(task1);
    }

    @Test
    public void TaskService_ChangeStatusWithStatusInWork(){
        when(taskRepository.findById(task1.getId())).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        taskService.changeStatus(task1.getId());

        assertEquals(Status.DONE, task1.getStatus());
        verify(taskRepository).save(task1);
    }

    @Test
    public void TaskService_ChangeStatusWithStatusDone(){
        when(taskRepository.findById(task2.getId())).thenReturn(Optional.of(task2));
        when(taskRepository.save(any(Task.class))).thenReturn(task2);

        taskService.changeStatus(task2.getId());

        assertEquals(Status.IN_WORK, task2.getStatus());
        verify(taskRepository).save(task2);
    }
}
