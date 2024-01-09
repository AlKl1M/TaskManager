package com.alkl1m.taskmanager.service.task;

import com.alkl1m.taskmanager.dto.TaskDto;

import java.util.List;

public interface TaskService {
    List<TaskDto> getTasksByProjectId(Long projectId);
    TaskDto postTask(TaskDto taskDto, Long projectId);
    void deleteTask(Long taskId);
    TaskDto updateTask(Long taskId, TaskDto taskDto);
}
