insert into taskmanager.t_users (id, c_name, c_email, c_password, c_role, c_enabled)
values (1, 'John Doe', 'john.doe@example.com', '$2a$10$qTh9ztQ9gO/mF2rMFO.1hOoV01zkC9aP4k96jWHp3O43uFQs5eMyq', 1, true);

INSERT INTO taskmanager.t_projects (id, c_name, c_description, c_created_at, c_done_at, c_status, c_user_id)
VALUES  (1, 'Project 1', 'Description for Project 1', '2022-01-01', null, 'IN_WORK', 1),
        (2, 'Project 2', 'Description for Project 2', '2022-02-01', null, 'IN_WORK', 1),
        (3, 'Project 3', 'Description for Project 3', '2022-03-01', null, 'IN_WORK', 1),
        (4, 'Project 4', 'Description for Project 4', '2022-04-01', null, 'IN_WORK', 1),
        (5, 'Project 5', 'Description for Project 5', '2022-05-01', null, 'IN_WORK', 1);

