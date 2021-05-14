alter table course
    add grouping_method enum ('NOT_GROUPING','FREE','AVERAGE','STRICT_CONTROLLED') default 'NOT_GROUPING' not null;
