package com.alkl1m.taskmanager.entity;

import com.alkl1m.taskmanager.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "taskmanager", name="t_tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "c_name")
    private String name;
    @Column(name = "c_description")
    private String description;
    @Column(name="c_created_at")
    private Instant createdAt;
    @Column(name="c_done_at")
    private Instant doneAt;
    @Column(name="c_deadline")
    private Instant deadline;
    @Column(name="c_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name="c_tags")
    private String tags;

    @ManyToOne(fetch= FetchType.LAZY, optional=false)
    @JoinColumn(name="c_project_id", nullable = false)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JsonIgnore
    private Project project;

    @ManyToOne(fetch= FetchType.LAZY, optional=false)
    @JoinColumn(name="c_user_id", nullable = false)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;
}

