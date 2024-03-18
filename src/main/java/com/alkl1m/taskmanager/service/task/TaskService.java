package com.alkl1m.taskmanager.service.task;

import com.alkl1m.taskmanager.controller.payload.task.CreateBackTaskDto;
import com.alkl1m.taskmanager.controller.payload.task.CreateTaskCommand;
import com.alkl1m.taskmanager.controller.payload.task.TaskDto;
import com.alkl1m.taskmanager.controller.payload.task.UpdateTaskCommand;

import java.util.List;

public interface TaskService {
    List<CreateBackTaskDto> getAllTasksBySearchWord(Long userId, Long projectId, String searchWord);
    List<CreateBackTaskDto> getAllTasksByTag(Long userId, Long projectId, String tag);

    TaskDto create(CreateTaskCommand cmd, Long projectId);
    TaskDto update(UpdateTaskCommand cmd);
    void delete(Long id);
    void changeStatus(Long id);
}