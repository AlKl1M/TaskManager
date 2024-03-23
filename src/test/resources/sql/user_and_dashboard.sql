insert into taskmanager.t_users (id, c_name, c_email, c_password, c_role, c_enabled)
values (1, 'John Doe', 'john.doe@example.com', '$2a$10$qTh9ztQ9gO/mF2rMFO.1hOoV01zkC9aP4k96jWHp3O43uFQs5eMyq', 1, true);

INSERT INTO taskmanager.t_projects (id, c_name, c_description, c_created_at, c_done_at, c_status, c_user_id)
VALUES  (1, 'Project 1', 'Description for Project 1', '2022-01-01', null, 'IN_WORK', 1),
        (2, 'Project 2', 'Description for Project 2', '2022-02-01', null, 'IN_WORK', 1);

INSERT INTO taskmanager.t_tasks (id, c_name, c_description, c_created_at, c_done_at, c_deadline, c_status, c_tags, c_project_id, c_user_id)
VALUES (1, 'Task 1', 'Description for Task 1', '2022-01-01', null, null, 'IN_WORK', '', 1, 1),
       (2, 'Task 2', 'Description for Task 2', '2022-02-01', null, null, 'IN_WORK', '', 1, 1);



