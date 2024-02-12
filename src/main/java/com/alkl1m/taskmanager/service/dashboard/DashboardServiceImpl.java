package com.alkl1m.taskmanager.service.dashboard;

import com.alkl1m.taskmanager.dto.dashboard.DashboardDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    @Override
    public DashboardDto getDashboardData(Long userId) {
        List<Project> projects = projectRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
        List<Task> tasks = taskRepository.findTop50ByUserIdOrderByDeadlineAsc(userId);

        return new DashboardDto(projects, tasks);
    }
}
