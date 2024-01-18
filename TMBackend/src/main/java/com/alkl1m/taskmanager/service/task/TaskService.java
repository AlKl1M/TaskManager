package com.alkl1m.taskmanager.service.task;

import com.alkl1m.taskmanager.dto.task.CreateTaskCommand;
import com.alkl1m.taskmanager.dto.task.UpdateTaskCommand;
import com.alkl1m.taskmanager.dto.task.FindTasksQuery;
import com.alkl1m.taskmanager.dto.task.TaskDto;
import com.alkl1m.taskmanager.dto.task.TasksPagedResult;

public interface TaskService {
    TasksPagedResult<TaskDto> findTasks(FindTasksQuery query, Long projectId, Long userId);
    TaskDto create(CreateTaskCommand cmd, Long projectId, Long userId);
    void update(UpdateTaskCommand cmd, Long projectId, Long userId);
    void delete(Long id, Long userId);
    void changeStatus(Long id, Long userId);
}
