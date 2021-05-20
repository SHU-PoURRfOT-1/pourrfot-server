alter table oss_file
    alter column metadata set default (json_array());
