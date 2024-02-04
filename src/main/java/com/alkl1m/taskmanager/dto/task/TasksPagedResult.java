package com.alkl1m.taskmanager.dto.task;

import java.util.List;

public record TasksPagedResult<CreateBackTaskRequest>(
        List<CreateBackTaskRequest> data
        ) {}
