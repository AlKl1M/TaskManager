package com.alkl1m.taskmanager.dto.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record CreateTaskCommand(String name,
                                String description,
                                List<String> tags
) {
}