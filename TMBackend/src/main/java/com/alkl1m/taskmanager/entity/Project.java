package com.alkl1m.taskmanager.entity;

import com.alkl1m.taskmanager.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@Table(name="projects")
public class Project implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(name="created_at", nullable = false)
    private Instant createdAt;
    @Column(name="done_at")
    private Instant doneAt;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Task> tasks;

    @ManyToOne(fetch= FetchType.LAZY, optional=false)
    @JoinColumn(name="user_id", nullable = false)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;
}
