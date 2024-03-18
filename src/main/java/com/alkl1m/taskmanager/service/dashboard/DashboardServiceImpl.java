package com.alkl1m.taskmanager.service.dashboard;

import com.alkl1m.taskmanager.controller.payload.dashboard.DashboardDto;
import com.alkl1m.taskmanager.controller.payload.dashboard.DashboardProjectDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    @Override
    public DashboardDto getDashboardData(Long userId) {
        List<Project> projects = projectRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Task> tasks = taskRepository.findTop50ByUserIdOrderByDeadlineAsc(userId);

        List<DashboardProjectDto> dashboardProjects = new ArrayList<>();
        for(Project project : projects) {
            int completedTasks = taskRepository.countByProjectIdAndStatus(project.getId(), Status.DONE);
            int totalTasks = taskRepository.countByProjectId(project.getId());
            DashboardProjectDto dashboardProject = DashboardProjectDto.from(project, completedTasks, totalTasks);
            dashboardProjects.add(dashboardProject);
        }
        return new DashboardDto(dashboardProjects, tasks);
    }
}
