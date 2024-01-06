package com.alkl1m.taskmanager.service.client;

import com.alkl1m.taskmanager.dto.ProjectDto;
import com.alkl1m.taskmanager.dto.TaskDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.TaskRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService{
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream().map(Project::getProjectDto).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getProjectsByName(String name) {
        return projectRepository.findAllByNameContaining(name).stream().map(Project::getProjectDto).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getProjectsByUserEmail(String email) {
        User user = userRepository.findByEmail(email);
        List<Project> projects = projectRepository.findByUser(user);
        return projects.stream()
                .map(Project::getProjectDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getTasksByProjectId(Long projectId, String email) {
        User user = userRepository.findByEmail(email);
        List<Task> tasks = taskRepository.findByProjectIdAndUser(projectId, user);
        return tasks.stream()
                .map(Task::getTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDto postProject(ProjectDto projectDto, String email) {
        User user = userRepository.findByEmail(email);
        Project project = new Project();
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setCreationDate(projectDto.getCreationDate());
        project.setCompletionDate(projectDto.getCompletionDate());
        project.setStatus(projectDto.getStatus());
        project.setUser(user);
        Project postedProject = projectRepository.save(project);
        ProjectDto postedProjectDto = new ProjectDto();
        postedProjectDto.setId(postedProject.getId());
        return postedProjectDto;
    }

    @Override
    public void deleteProject(Long projectId, String email) {
        Optional<Project> optionalProject = projectRepository.findByUserEmailAndId(email, projectId);
        if (optionalProject.isPresent()) {
            projectRepository.deleteById(optionalProject.get().getId());
        } else {
            throw new IllegalArgumentException("Project with id: " + projectId + " not found");
        }
    }

    @Override
    public ProjectDto updateProject(Long projectId, ProjectDto projectDto, String email) {
        Optional<Project> optionalProject = projectRepository.findByUserEmailAndId(email, projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            project.setName(projectDto.getName());
            project.setDescription(projectDto.getDescription());
            project.setCreationDate(projectDto.getCreationDate());
            project.setCompletionDate(projectDto.getCompletionDate());
            project.setStatus(projectDto.getStatus());
            Project updatedProject = projectRepository.save(project);
            return updatedProject.getProjectDto();
        }
        return null;
    }

    @Override
    public TaskDto postTask(TaskDto taskDto, String email, Long projectId) {
        User user = userRepository.findByEmail(email);
        Task task = new Task();
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            task.setName(taskDto.getName());
            task.setDescription(taskDto.getDescription());
            task.setCreationDate(taskDto.getCreationDate());
            task.setCompletionDate(taskDto.getCompletionDate());
            task.setStatus(taskDto.getStatus());
            task.setUser(user);
            task.setProject(project);
            Task postedTask = taskRepository.save(task);
            TaskDto postedTaskDto = new TaskDto();
            postedTaskDto.setId(postedTask.getId());
            return postedTaskDto;
        }
        return null;
    }

    @Override
    public void deleteTask(Long taskId, String email) {
        Optional<Task> optionalTask = taskRepository.findByUserEmailAndId(email, taskId);
        if (optionalTask.isPresent()) {
            taskRepository.deleteById(optionalTask.get().getId());
        } else {
            throw new IllegalArgumentException("Task with id: " + taskId + " not found");
        }
    }

    @Override
    public TaskDto updateTask(Long taskId, TaskDto taskDto, String email) {
       Optional<Task> optionalTask = taskRepository.findByUserEmailAndId(email, taskId);
       if (optionalTask.isPresent()) {
           Task task = optionalTask.get();
           task.setName(taskDto.getName());
           task.setDescription(taskDto.getDescription());
           task.setCreationDate(taskDto.getCreationDate());
           task.setCompletionDate(taskDto.getCompletionDate());
           task.setStatus(taskDto.getStatus());
           Task updatedTask = taskRepository.save(task);
           return updatedTask.getTaskDto();
       }
       return null;
    }
}
