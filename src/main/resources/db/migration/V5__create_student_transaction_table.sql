create table student_transaction
(
    id          int auto_increment
        primary key,
    create_time datetime   default CURRENT_TIMESTAMP not null,
    update_time datetime   default CURRENT_TIMESTAMP not null,
    sender      int                                  not null,
    receiver    int                                  not null,
    title       varchar(128)                         not null,
    urgent      tinyint(1) default 0                 not null,
    content     text                                 not null,
    metadata    json       default (json_object())   not null
);
