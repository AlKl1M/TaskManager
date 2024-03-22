create schema if not exists taskmanager;

create table taskmanager.t_users (
                                     id serial primary key,
                                     c_name varchar(255) not null check ( length(c_name) between 3 and 20),
                                     c_email varchar(255) not null check ( length(c_email) between 3 and 60),
                                     c_password varchar(255) not null check ( length(c_password) between 3 and 80),
                                     c_role int check (c_role between 0 and 1),
                                     c_enabled boolean,
                                     unique (c_name),
                                     unique (c_email)
);
create table taskmanager.t_projects (
                                        id serial primary key,
                                        c_name varchar(255) not null check ( length(c_name) between 3 and 60),
                                        c_description varchar(255) not null check ( length(c_description) between 3 and 90),
                                        c_created_at timestamp(6) with time zone not null,
                                        c_done_at timestamp(6) with time zone,
                                        c_status varchar(255) not null check (c_status in ('DONE', 'IN_WORK')),
                                        c_user_id int not null,
                                        foreign key (c_user_id) references taskmanager.t_users on delete cascade
);
create table taskmanager.t_tasks (
                                     id serial primary key,
                                     c_name varchar(255) not null check ( length(c_name) between 3 and 60),
                                     c_description varchar(255) not null check ( length(c_description) between 3 and 90),
                                     c_created_at timestamp(6) with time zone not null,
                                     c_done_at timestamp(6) with time zone,
                                     c_deadline timestamp(6) with time zone check (c_deadline > CURRENT_DATE),
                                     c_status varchar(255) not null check (c_status in ('DONE', 'IN_WORK')),
                                     c_tags varchar(255),
                                     c_project_id int not null,
                                     c_user_id int not null,
                                     foreign key (c_project_id) references taskmanager.t_projects on delete cascade,
                                     foreign key (c_user_id) references taskmanager.t_users on delete cascade
);
create table taskmanager.t_password_reset_token (
                                                    id serial primary key,
                                                    c_token varchar(255) not null unique,
                                                    c_user_id int unique,
                                                    c_expiry_date timestamp(6) not null,
                                                    foreign key (c_user_id) references taskmanager.t_users
);
create table taskmanager.t_refresh_token (
                                             id serial primary key,
                                             c_user_id int not null,
                                             c_token varchar(255) not null unique,
                                             c_expiry_date timestamp(6) with time zone not null,
                                             foreign key (c_user_id) references taskmanager.t_users on delete cascade
);
create table taskmanager.t_verification_token (
                                                  token_id serial primary key,
                                                  c_user_id int unique,
                                                  c_token varchar(255) unique,
                                                  c_expiration_time timestamp(6),
                                                  foreign key (c_user_id) references taskmanager.t_users
);