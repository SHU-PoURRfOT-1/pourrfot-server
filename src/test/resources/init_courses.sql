INSERT INTO pourrfot.course (id, create_time, update_time, teacher_id, course_code, course_name, class_time,
                             class_location, term, profile_photo)
VALUES (1, '2021-04-14 12:25:09', '2021-04-14 12:25:09', 1, '4ZS081001', '软件项目管理', '三[6-9](1-10周)', '嘉定校区1-331',
        '202003', '');
INSERT INTO pourrfot.course (id, create_time, update_time, teacher_id, course_code, course_name, class_time,
                             class_location, term, profile_photo)
VALUES (2, '2021-04-14 12:25:48', '2021-04-14 12:25:48', 2, '4ZS081002', '软件项目管理', '四[6-9](1-10周)', '嘉定校区1-331',
        '202003', '');
INSERT INTO pourrfot.course (id, create_time, update_time, teacher_id, course_code, course_name, class_time,
                             class_location, term, profile_photo)
VALUES (3, '2021-04-15 15:27:31', '2021-04-15 15:27:31', 1, '3ZS081002', '软件测试技术及应用', '一[6-9](1-10周)\\', '嘉定校区1-209
', '202003', '');
INSERT INTO pourrfot.course (id, create_time, update_time, teacher_id, course_code, course_name, class_time,
                             class_location, term, profile_photo)
VALUES (4, '2021-04-15 15:27:31', '2021-04-15 15:27:31', 2, '3ZS081004', '软件测试技术及应用', '二[6-9](1-10周)\\', '嘉定校区1-209
', '202003', '');
INSERT INTO pourrfot.course (id, create_time, update_time, teacher_id, course_code, course_name, class_time,
                             class_location, term, profile_photo)
VALUES (5, '2021-04-15 15:27:31', '2021-04-15 15:27:31', 1, '2ZS081003', '软件体系结构', '一[6-9](1-10周)', '嘉定校区1-233',
        '202002', '');
INSERT INTO pourrfot.course (id, create_time, update_time, teacher_id, course_code, course_name, class_time,
                             class_location, term, profile_photo)
VALUES (6, '2021-04-15 15:27:31', '2021-04-15 15:27:31', 2, '2ZS081004', '软件体系结构', '二[6-9](1-10周)', '嘉定校区1-233',
        '202002', '');
INSERT INTO pourrfot.course (id, create_time, update_time, teacher_id, course_code, course_name, class_time,
                             class_location, term, profile_photo)
VALUES (7, '2021-04-15 15:27:31', '2021-04-15 15:27:31', 1, '3ZS081003', '企业级应用开发技术', '五[7-10](1-10周)', '嘉定校区1-231',
        '202001', '');
INSERT INTO pourrfot.course (id, create_time, update_time, teacher_id, course_code, course_name, class_time,
                             class_location, term, profile_photo)
VALUES (8, '2021-04-15 15:29:38', '2021-04-15 15:29:38', 2, '3ZS081001', '软件建模方法', '三[6-9](1-10周)', '嘉定校区1-207',
        '202002', '');
INSERT INTO pourrfot.course (id, create_time, update_time, teacher_id, course_code, course_name, class_time,
                             class_location, term, profile_photo)
VALUES (9, '2021-04-15 15:29:38', '2021-04-15 15:29:38', 1, '2ZS081002', '软件形式方法基础', '五[1-4](1-10周)', '嘉定校区1-233',
        '202002', '');
INSERT INTO pourrfot.course (id, create_time, update_time, teacher_id, course_code, course_name, class_time,
                             class_location, term, profile_photo)
VALUES (10, '2021-04-15 15:32:24', '2021-04-15 15:32:24', 2, '3ZSL08401', '操作系统结构与分析', '二[7-10](1-10周)', '嘉定校区1-411',
        '202002', '');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    password)
VALUES (1, '2021-04-16 09:15:51', '2021-04-16 09:15:51', 'caomin', '曹旻', '', '1970-01-01 00:00:00', 'female',
        'teacher', 'caomin');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    password)
VALUES (2, '2021-04-16 09:18:07', '2021-04-16 09:18:07', 'chenyihai', '陈怡海', '', '1970-01-01 00:00:00', 'male',
        'teacher', 'chenyihai');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (3, '2021-04-19 00:20:09', '2021-04-19 00:20:09', 'spencercjh', '蔡佳昊', '', '1998-03-25', 'male', 'student',
        'shouspencercjh@foxmail.com', '15000131965', '123456');
INSERT INTO `pourrfot_user` (`id`, `create_time`, `update_time`, `username`, `nickname`, `profile_photo`, `birth`,
                             `sex`, `role`, `email`, `telephone`, `password`)
VALUES (4, '2021-05-12 14:46:03', '2021-05-12 14:46:03', 'admin', 'admin-test', '', null, 'unknown', 'admin', '', '',
        'admin');
INSERT INTO `pourrfot_user` (`id`, `create_time`, `update_time`, `username`, `nickname`, `profile_photo`, `birth`,
                             `sex`, `role`, `email`, `telephone`, `password`)
VALUES (5, '2021-05-13 14:08:19', '2021-05-13 14:08:19', 'student', 'student-test', '', null, 'unknown', 'student', '',
        '', 'student');
INSERT INTO `pourrfot_user` (`id`, `create_time`, `update_time`, `username`, `nickname`, `profile_photo`, `birth`,
                             `sex`, `role`, `email`, `telephone`, `password`)
VALUES (6, '2021-05-13 14:09:07', '2021-05-13 14:09:07', 'teacher', 'teacher-test', '', null, 'unknown', 'teacher', '',
        '', 'teacher');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (7, '2021-05-18 23:50:09', '2021-05-18 23:50:09', 'abc', 'abc', '', null, 'male', 'student', '', '', 'abc');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (8, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'a', 'student-a', '', '1998-03-25', 'male', 'student', '', '',
        'a');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (9, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'b', 'student-b', '', '1998-03-25', 'male', 'student', '', '',
        'b');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (10, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'c', 'student-c', '', '1998-03-25', 'male', 'student', '', '',
        'c');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (11, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'd', 'student-d', '', '1998-03-25', 'male', 'student', '', '',
        'd');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (12, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'e', 'student-e', '', '1998-03-25', 'male', 'student', '', '',
        'e');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (13, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'f', 'student-f', '', '1998-03-25', 'male', 'student', '', '',
        'f');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (14, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'g', 'student-g', '', '1998-03-25', 'male', 'student', '', '',
        'g');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (15, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'h', 'student-h', '', '1998-03-25', 'male', 'student', '', '',
        'h');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (16, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'i', 'student-i', '', '1998-03-25', 'male', 'student', '', '',
        'i');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (17, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'j', 'student-j', '', '1998-03-25', 'male', 'student', '', '',
        'j');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (18, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'k', 'student-k', '', '1998-03-25', 'male', 'student', '', '',
        'k');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (19, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'l', 'student-l', '', '1998-03-25', 'male', 'student', '', '',
        'l');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (20, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'm', 'student-m', '', '1998-03-25', 'male', 'student', '', '',
        'm');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (21, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'n', 'student-n', '', '1998-03-25', 'female', 'student', '',
        '', 'n');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (22, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'o', 'student-o', '', '1998-03-25', 'female', 'student', '',
        '', 'o');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (23, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'p', 'student-p', '', '1998-03-25', 'female', 'student', '',
        '', 'p');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (24, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'q', 'student-q', '', '1998-03-25', 'female', 'student', '',
        '', 'q');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (25, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'r', 'student-r', '', '1998-03-25', 'female', 'student', '',
        '', 'r');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (26, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 's', 'student-s', '', '1998-03-25', 'female', 'student', '',
        '', 's');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (27, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 't', 'student-t', '', '1998-03-25', 'female', 'student', '',
        '', 't');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (28, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'u', 'student-u', '', '1998-03-25', 'female', 'student', '',
        '', 'u');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (29, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'v', 'student-v', '', '1998-03-25', 'female', 'student', '',
        '', 'v');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (30, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'w', 'student-w', '', '1998-03-25', 'female', 'student', '',
        '', 'w');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (31, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'x', 'student-x', '', '1998-03-25', 'female', 'student', '',
        '', 'x');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (32, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'y', 'student-y', '', '1998-03-25', 'female', 'student', '',
        '', 'y');
INSERT INTO pourrfot.pourrfot_user (id, create_time, update_time, username, nickname, profile_photo, birth, sex, role,
                                    email, telephone, password)
VALUES (33, '2021-05-18 23:39:23', '2021-05-18 23:39:23', 'z', 'student-z', '', '1998-03-25', 'female', 'student', '',
        '', 'z');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (1, '2021-04-20 19:35:34', '2021-04-20 19:35:34', 1, '软件项目管理-第1小组', 'profilePhoto');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (2, '2021-05-14 01:20:48', '2021-05-14 01:20:48', 2, 'mock-2-1', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (3, '2021-05-14 01:20:48', '2021-05-14 01:20:48', 3, 'mock-3-1', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (4, '2021-05-14 01:20:48', '2021-05-14 01:20:48', 4, 'mock-4-1', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (5, '2021-05-14 01:20:48', '2021-05-14 01:20:48', 5, 'mock-5-1', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (6, '2021-05-14 01:20:48', '2021-05-14 01:20:48', 6, 'mock-6-1', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (7, '2021-05-14 01:20:48', '2021-05-14 01:20:48', 7, 'mock-7-1', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (8, '2021-05-14 01:20:48', '2021-05-14 01:20:48', 8, 'mock-8-1', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (9, '2021-05-14 01:20:48', '2021-05-14 01:20:48', 9, 'mock-9-1', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (10, '2021-05-14 01:20:48', '2021-05-14 01:20:48', 10, 'mock-10-1', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (11, '2021-05-18 22:38:41', '2021-05-18 22:38:41', 1, '软件项目管理-第2小组', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (12, '2021-05-18 22:38:41', '2021-05-18 22:38:41', 1, '软件项目管理-第3小组', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (13, '2021-05-18 22:38:41', '2021-05-18 22:38:41', 1, '软件项目管理-第4小组', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (14, '2021-05-18 22:38:41', '2021-05-18 22:38:41', 1, '软件项目管理-第5小组', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (15, '2021-05-18 22:38:41', '2021-05-18 22:38:41', 1, '软件项目管理-第6小组', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (16, '2021-05-18 22:38:41', '2021-05-18 22:38:41', 1, '软件项目管理-第7小组', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (17, '2021-05-18 22:38:41', '2021-05-18 22:38:41', 1, '软件项目管理-第8小组', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (18, '2021-05-18 22:38:41', '2021-05-18 22:38:41', 1, '软件项目管理-第9小组', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (19, '2021-05-18 22:38:41', '2021-05-18 22:38:41', 1, '软件项目管理-第10小组', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (20, '2021-05-18 22:38:41', '2021-05-18 22:38:41', 1, '软件项目管理-第11小组', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (21, '2021-05-18 22:38:41', '2021-05-18 22:38:41', 1, '软件项目管理-第12小组', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (22, '2021-05-18 22:38:41', '2021-05-18 22:38:41', 1, '软件项目管理-第13小组', '');
INSERT INTO pourrfot.course_group (id, create_time, update_time, course_id, group_name, profile_photo)
VALUES (23, '2021-05-18 22:38:41', '2021-05-18 22:38:41', 1, '软件项目管理-第14小组', '');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (1, '2021-05-14 01:16:11', '2021-05-14 01:16:11', 3, '蔡佳昊', 1, 1, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (2, '2021-05-14 01:16:11', '2021-05-14 01:16:11', 5, 'student-test', 2, 2, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (3, '2021-05-14 01:16:11', '2021-05-14 01:16:11', 3, '蔡佳昊', 3, 3, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (4, '2021-05-14 01:16:11', '2021-05-14 01:16:11', 5, 'student-test', 4, 4, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (5, '2021-05-14 01:16:11', '2021-05-14 01:16:11', 3, '蔡佳昊', 5, 5, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (6, '2021-05-14 01:16:11', '2021-05-14 01:16:11', 5, 'student-test', 6, 6, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (7, '2021-05-14 01:16:11', '2021-05-14 01:16:11', 3, '蔡佳昊', 7, 7, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (8, '2021-05-14 01:16:11', '2021-05-14 01:16:11', 5, 'student-test', 8, 8, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (9, '2021-05-14 01:16:11', '2021-05-14 01:16:11', 3, '蔡佳昊', 8, 9, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (10, '2021-05-14 01:16:11', '2021-05-14 01:16:11', 5, 'student-test', 10, 10, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (11, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 7, 'abc', 1, 11, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (12, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 8, 'student-a', 1, 12, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (13, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 9, 'student-b', 1, 13, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (14, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 10, 'student-c', 1, 14, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (15, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 11, 'student-d', 1, 15, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (16, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 12, 'student-e', 1, 16, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (17, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 13, 'student-f', 1, 17, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (18, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 14, 'student-g', 1, 18, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (19, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 15, 'student-h', 1, 19, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (20, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 16, 'student-i', 1, 20, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (21, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 17, 'student-j', 1, 21, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (22, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 18, 'student-k', 1, 22, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (23, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 19, 'student-l', 1, 23, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (24, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 20, 'student-m', 1, 11, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (25, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 21, 'student-n', 1, 12, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (26, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 22, 'student-o', 1, 13, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (27, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 23, 'student-p', 1, 14, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (28, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 24, 'student-q', 1, 15, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (29, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 25, 'student-r', 1, 16, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (30, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 26, 'student-s', 1, 17, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (31, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 27, 'student-t', 1, 18, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (32, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 28, 'student-u', 1, 19, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (33, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 29, 'student-v', 1, 20, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (34, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 30, 'student-w', 1, 21, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (35, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 31, 'student-x', 1, 22, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (36, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 32, 'student-y', 1, 23, 0, '[]');
INSERT INTO pourrfot.course_student (id, create_time, update_time, student_id, student_name, course_id, group_id,
                                     total_score, score_structure)
VALUES (37, '2021-05-18 23:53:18', '2021-05-18 23:53:18', 33, 'student-z', 1, 1, 0, '[]');
INSERT INTO pourrfot.project (id, create_time, update_time, project_name, project_code, owner_id, profile_photo)
VALUES (1, '2021-05-16 22:54:42', '2021-05-16 22:54:42', '科创项目1', 'PROJECT-1', 1, '');
INSERT INTO pourrfot.project (id, create_time, update_time, project_name, project_code, owner_id, profile_photo)
VALUES (2, '2021-05-16 22:54:42', '2021-05-16 22:54:42', '科创项目2', 'PROJECT-2', 2, '');
INSERT INTO pourrfot.project (id, create_time, update_time, project_name, project_code, owner_id, profile_photo)
VALUES (3, '2021-05-16 22:54:42', '2021-05-16 22:54:42', '科创项目3', 'PROJECT-3', 1, '');
INSERT INTO pourrfot.project (id, create_time, update_time, project_name, project_code, owner_id, profile_photo)
VALUES (4, '2021-05-16 22:54:42', '2021-05-16 22:54:42', '科创项目4', 'PROJECT-4', 2, '');
INSERT INTO pourrfot.project (id, create_time, update_time, project_name, project_code, owner_id, profile_photo)
VALUES (5, '2021-05-16 22:54:42', '2021-05-16 22:54:42', '科创项目5', 'PROJECT-5', 6, '');
INSERT INTO pourrfot.project_member (id, create_time, update_time, project_id, user_id, role_name)
VALUES (1, '2021-05-16 23:30:10', '2021-05-16 23:30:10', 1, 1, 'owner');
INSERT INTO pourrfot.project_member (id, create_time, update_time, project_id, user_id, role_name)
VALUES (2, '2021-05-16 23:30:10', '2021-05-16 23:30:10', 2, 2, 'owner');
INSERT INTO pourrfot.project_member (id, create_time, update_time, project_id, user_id, role_name)
VALUES (3, '2021-05-16 23:30:10', '2021-05-16 23:30:10', 3, 1, 'owner');
INSERT INTO pourrfot.project_member (id, create_time, update_time, project_id, user_id, role_name)
VALUES (4, '2021-05-16 23:30:10', '2021-05-16 23:30:10', 4, 2, 'owner');
INSERT INTO pourrfot.project_member (id, create_time, update_time, project_id, user_id, role_name)
VALUES (5, '2021-05-16 23:30:10', '2021-05-16 23:30:10', 5, 6, 'owner');
INSERT INTO pourrfot.project_member (id, create_time, update_time, project_id, user_id, role_name)
VALUES (6, '2021-05-16 23:30:10', '2021-05-16 23:30:10', 1, 3, 'member');
INSERT INTO pourrfot.project_member (id, create_time, update_time, project_id, user_id, role_name)
VALUES (7, '2021-05-16 23:30:10', '2021-05-16 23:30:10', 2, 5, 'member');
INSERT INTO pourrfot.project_member (id, create_time, update_time, project_id, user_id, role_name)
VALUES (8, '2021-05-16 23:30:10', '2021-05-16 23:30:10', 3, 3, 'member');
INSERT INTO pourrfot.project_member (id, create_time, update_time, project_id, user_id, role_name)
VALUES (9, '2021-05-16 23:30:10', '2021-05-16 23:30:10', 4, 5, 'member');
