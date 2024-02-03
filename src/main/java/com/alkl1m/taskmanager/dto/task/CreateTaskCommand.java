package com.alkl1m.taskmanager.dto.task;

import java.util.ArrayList;
import java.util.Set;

public record CreateTaskCommand(String name,
                                String description,
                                ArrayList<String> tags
) {
}