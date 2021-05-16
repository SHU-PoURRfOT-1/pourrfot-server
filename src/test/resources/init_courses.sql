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
