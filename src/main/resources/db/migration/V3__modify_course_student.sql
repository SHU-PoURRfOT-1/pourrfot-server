alter table course_student
    add student_name varchar(32) not null after student_id;

alter table course_student
    drop column part_one_score;

alter table course_student
    drop column part_one_score_weight;

alter table course_student
    drop column part_two_score;

alter table course_student
    drop column part_two_score_weight;

alter table course_student
    drop column part_three_score;

alter table course_student
    drop column part_three_score_weight;

alter table course_student
    drop column part_four_score;

alter table course_student
    drop column part_four_score_weight;

alter table course_student
    drop column part_five_score;

alter table course_student
    drop column part_five_score_weight;

alter table course_student
    add score_structure json default (JSON_ARRAY()) not null;

alter table pourrfot_user
    modify birth date null;

alter table course_student
    add group_id int null after course_id;

drop table course_group_student;
