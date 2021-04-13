create table pourrfot_user
(
    id            int auto_increment primary key,
    create_time   datetime     default CURRENT_TIMESTAMP not null,
    update_time   datetime     default CURRENT_TIMESTAMP not null,
    username      varchar(64)  default '' unique         not null,
    nickname      varchar(36)  default ''                not null,
    profile_photo varchar(255) default ''                not null,
    birth         datetime null,
    sex           enum ('male','female','unknown') default 'unknown' not null,
    role          enum ('guest','student','teacher','admin') default 'guest' not null
);

create table course
(
    id             int auto_increment primary key,
    create_time    datetime     default CURRENT_TIMESTAMP not null,
    update_time    datetime     default CURRENT_TIMESTAMP not null,
    teacher_id     int                                    not null,
    course_code    varchar(32)  default '' unique         not null,
    course_name    varchar(64)  default ''                not null,
    class_time     varchar(32)  default ''                not null,
    class_location varchar(32)  default ''                not null,
    term           varchar(16)  default ''                not null,
    profile_photo  varchar(255) default ''                not null
);

create table course_student
(
    id                      int auto_increment primary key,
    create_time             datetime default CURRENT_TIMESTAMP not null,
    update_time             datetime default CURRENT_TIMESTAMP not null,
    student_id              int                                not null,
    course_id               int                                not null,
    part_one_score          decimal  default 0                 not null,
    part_one_score_weight   decimal  default 1                 not null,
    part_two_score          decimal  default 0                 not null,
    part_two_score_weight   decimal  default 1                 not null,
    part_three_score        decimal  default 0                 not null,
    part_three_score_weight decimal  default 1                 not null,
    part_four_score         decimal  default 0                 not null,
    part_four_score_weight  decimal  default 1                 not null,
    part_five_score         decimal  default 0                 not null,
    part_five_score_weight  decimal  default 1                 not null,
    total_score             decimal  default 0                 not null
);

create table course_group
(
    id            int auto_increment primary key,
    create_time   datetime     default CURRENT_TIMESTAMP not null,
    update_time   datetime     default CURRENT_TIMESTAMP not null,
    course_id     int                                    not null,
    group_name    varchar(64)  default ''                not null,
    profile_photo varchar(255) default ''                not null
);

create table course_group_student
(
    id          int auto_increment primary key,
    create_time datetime default CURRENT_TIMESTAMP not null,
    update_time datetime default CURRENT_TIMESTAMP not null,
    group_id    int                                not null,
    course_id   int                                not null,
    student_id  int                                not null
);

create table project
(
    id            int auto_increment primary key,
    create_time   datetime     default CURRENT_TIMESTAMP not null,
    update_time   datetime     default CURRENT_TIMESTAMP not null,
    project_name  varchar(64)  default ''                not null,
    project_code  varchar(64)  default '' unique         not null,
    owner_id      int                                    not null,
    profile_photo varchar(255) default ''                not null
);

create table project_user
(
    id          int auto_increment primary key,
    create_time datetime default CURRENT_TIMESTAMP not null,
    update_time datetime default CURRENT_TIMESTAMP not null,
    project_id  int                                not null,
    user_id     int                                not null,
    role_name   varchar(32)                        not null
        comment 'role in the project'
);
