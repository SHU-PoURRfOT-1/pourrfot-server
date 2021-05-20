alter table course
    add score_structure json default (json_array()) not null;

alter table course_student
    change score_structure detail_score json default (json_array()) not null;
