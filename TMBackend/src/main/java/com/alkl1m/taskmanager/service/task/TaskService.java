package com.alkl1m.taskmanager.service.task;

import com.alkl1m.taskmanager.dto.*;

public interface TaskService {
    TasksPagedResult<TaskDto> findTasks(FindTasksQuery query, Long projectId);
    TaskDto create(CreateTaskCommand cmd, Long projectId);
    void update(UpdateTaskCommand cmd, Long projectId);
    void delete(Long id);
}
