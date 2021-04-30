create table oss_file
(
    id          int auto_increment primary key,
    create_time datetime                                                            default CURRENT_TIMESTAMP                    not null,
    update_time datetime                                                            default CURRENT_TIMESTAMP                    not null,
    name        varchar(128)                                                        default 'file name without prefix directory' not null,
    metadata    json                                                                default (JSON_OBJECT())                      not null,
    type        enum ('courses', 'projects', 'transactions', 'messages', 'unknown') default 'unknown'                            not null,
    resource_id int comment 'course/project/transaction/message id'                                                              not null,
    directory   varchar(256)                                                        default ''                                   not null,
    oss_url     varchar(256)                                                        default ''                                   not null,
    owner_id    int comment 'uploader userid'                                                                                    not null
);

create table course_files
(
    id          int auto_increment primary key,
    create_time datetime default CURRENT_TIMESTAMP not null,
    update_time datetime default CURRENT_TIMESTAMP not null,
    course_id   int                                not null,
    file_id     int                                not null
);

create table project_files
(
    id          int auto_increment primary key,
    create_time datetime default CURRENT_TIMESTAMP not null,
    update_time datetime default CURRENT_TIMESTAMP not null,
    project_id  int                                not null,
    file_id     int                                not null
)
