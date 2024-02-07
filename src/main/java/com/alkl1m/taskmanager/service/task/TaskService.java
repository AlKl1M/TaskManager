package com.alkl1m.taskmanager.service.task;

import com.alkl1m.taskmanager.dto.task.*;

import java.util.List;

public interface TaskService {
    List<CreateBackTaskRequest> getAllTasks(Long userId, FindTasksTags findTags, Long projectId);

    TaskDto create(CreateTaskCommand cmd, Long projectId);
    TaskDto update(UpdateTaskCommand cmd, Long projectId);
    void delete(Long id);
    void changeStatus(Long id);
}
