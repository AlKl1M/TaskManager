package com.alkl1m.taskmanager.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate creationDate;
    private LocalDate completionDate;
    private String status;
}
