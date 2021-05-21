alter table course_student
    add student_number varchar(32) not null after student_id;
alter table course
    add group_size int default 0 not null;
