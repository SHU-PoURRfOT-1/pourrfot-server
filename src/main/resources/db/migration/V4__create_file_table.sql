create table oss_file
(
    id             int auto_increment primary key,
    create_time    datetime                                                                     default CURRENT_TIMESTAMP                    not null,
    update_time    datetime                                                                     default CURRENT_TIMESTAMP                    not null,
    name           varchar(128)                                                                 default 'file name without prefix directory' not null,
    metadata       json                                                                         default (json_object())                      not null,
    resource_type  enum ('courses', 'projects', 'transactions', 'messages','groups', 'unknown') default 'unknown'                            not null,
    resource_id    int                                                                                                                       null comment 'course/project/transaction/message id',
    directory      varchar(256)                                                                 default ''                                   not null,
    oss_key        varchar(256)                                                                 default ''                                   not null,
    oss_url        varchar(256)                                                                 default ''                                   not null comment 'complete oss url stated with oss://',
    origin_oss_url varchar(256)                                                                 default ''                                   not null,
    owner_id       int                                                                                                                       not null comment 'uploader userid'
);
