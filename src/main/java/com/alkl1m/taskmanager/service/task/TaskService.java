package com.alkl1m.taskmanager.service.task;

import com.alkl1m.taskmanager.dto.task.CreateTaskCommand;
import com.alkl1m.taskmanager.dto.task.UpdateTaskCommand;
import com.alkl1m.taskmanager.dto.task.FindTasksQuery;
import com.alkl1m.taskmanager.dto.task.TaskDto;
import com.alkl1m.taskmanager.dto.task.TasksPagedResult;

public interface TaskService {
    TasksPagedResult<TaskDto> findTasks(FindTasksQuery query, Long projectId);
    TaskDto create(CreateTaskCommand cmd, Long projectId);
    void update(UpdateTaskCommand cmd, Long projectId);
    void delete(Long id);
    void changeStatus(Long id);
}
