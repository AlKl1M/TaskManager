package com.alkl1m.taskmanager.service.task;

import com.alkl1m.taskmanager.dto.task.*;

public interface TaskService {
    TasksPagedResult<CreateBackTaskRequest> getAllTasks(FindTasksTags findTags, Long projectId);

    TaskDto create(CreateTaskCommand cmd, Long projectId);
    TaskDto update(UpdateTaskCommand cmd, Long projectId);
    void delete(Long id);
    void changeStatus(Long id);
}
