create table message
(
    id                int auto_increment primary key,
    create_time       datetime   default CURRENT_TIMESTAMP not null,
    update_time       datetime   default CURRENT_TIMESTAMP not null,
    parent_message_id int        default 0                 not null
        comment 'reply to a (parent) message. 0 represents nothing',
    sender            int                                  not null,
    receiver          int                                  not null,
    title             varchar(128)                         not null,
    urgent            tinyint(1) default 0                 not null,
    regular           tinyint(1) default 0                 not null,
    content           text                                 not null,
    metadata          json       default (json_object())   not null
);

create table inbox_message
(
    id          int auto_increment primary key,
    create_time datetime   default CURRENT_TIMESTAMP not null,
    update_time datetime   default CURRENT_TIMESTAMP not null,
    sender      int                                  not null,
    receiver    int                                  not null,
    message_id  int                                  not null,
    have_read   tinyint(1) default 0                 not null
);
