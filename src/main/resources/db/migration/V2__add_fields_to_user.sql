alter table pourrfot_user
    add email varchar(64) default '' not null;

alter table pourrfot_user
    add telephone varchar(64) default '' not null;

alter table pourrfot_user
    add password varchar(256) default '' not null;
