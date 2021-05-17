package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.ProjectMemberRoleEnum;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.filter.JwtAuthorizationFilter;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.Project;
import cn.edu.shu.pourrfot.server.model.ProjectMember;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectMapper;
import cn.edu.shu.pourrfot.server.service.ProjectMemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Slf4j
@Testcontainers(disabledWithoutDocker = true)
class ProjectMemberServiceImplTest {
  @ClassRule
  public static MySQLContainer<CustomMySQLContainer> customMySQLContainer = CustomMySQLContainer.getInstance();
  private final PourrfotUser teacher = PourrfotUser.builder()
    .username("teacher")
    .nickname("teacher-mock")
    .password("teacher")
    .role(RoleEnum.teacher)
    .id(100)
    .build();
  private final Map<String, Object> teacherDetail = Map.of("id", ((long) teacher.getId()), "username", "mock",
    "role", "teacher");
  private final PourrfotUser admin = PourrfotUser.builder()
    .username("admin")
    .nickname("admin-mock")
    .password("admin")
    .role(RoleEnum.admin)
    .id(300)
    .build();
  private final Map<String, Object> adminDetail = Map.of("id", ((long) admin.getId()), "username", "mock",
    "role", "admin");
  private final PourrfotUser mockStudent = PourrfotUser.builder()
    .id(200)
    .password("student")
    .nickname("student")
    .username("student")
    .role(RoleEnum.student)
    .build();
  private final Map<String, Object> studentDetail = Map.of("id", ((long) mockStudent.getId()), "username", "mock",
    "role", "student");
  private final UsernamePasswordAuthenticationToken mockAdminAuthenticationToken = new UsernamePasswordAuthenticationToken(
    "mock", "mock", List.of(new JwtAuthorizationFilter.SimpleGrantedAuthority(RoleEnum.admin)));
  private final UsernamePasswordAuthenticationToken mockTeacherAuthenticationToken = new UsernamePasswordAuthenticationToken(
    "mock", "mock", List.of(new JwtAuthorizationFilter.SimpleGrantedAuthority(RoleEnum.teacher)));
  private final UsernamePasswordAuthenticationToken mockStudentAuthenticationToken = new UsernamePasswordAuthenticationToken(
    "mock", "mock", List.of(new JwtAuthorizationFilter.SimpleGrantedAuthority(RoleEnum.student)));
  private final Project mockProject = Project.builder()
    .projectCode("MOCK")
    .projectName("MOCK")
    .profilePhoto("MOCK")
    .ownerId(teacher.getId())
    .id(400)
    .build();
  private final ProjectMember studentMember = ProjectMember.builder()
    .projectId(mockProject.getId())
    .roleName(ProjectMemberRoleEnum.MEMBER.name())
    .userId(mockStudent.getId())
    .build();
  private final ProjectMember teacherMember = ProjectMember.builder()
    .projectId(mockProject.getId())
    .roleName(ProjectMemberRoleEnum.OWNER.name())
    .userId(teacher.getId())
    .build();

  @Autowired
  private ProjectMemberService projectMemberService;
  @Autowired
  private ProjectMapper projectMapper;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.getContext().setAuthentication(null);
  }

  @Transactional
  @Test
  void save() {
    given(projectMapper.selectById(eq(mockProject.getId()))).willReturn(mockProject);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectMemberService.save(teacherMember));
  }

  @Transactional
  @Test
  void saveFailedBecauseProjectNotFound() {
    given(projectMapper.selectById(eq(mockProject.getId()))).willReturn(null);
    assertThrows(NotFoundException.class, () -> projectMemberService.save(teacherMember),
      "Can't create a project-member because the project doesn't exist");
  }

  @Transactional
  @Test
  void saveFailedBecauseUserNotFound() {
    given(projectMapper.selectById(eq(mockProject.getId()))).willReturn(mockProject);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(null);
    assertThrows(NotFoundException.class, () -> projectMemberService.save(teacherMember),
      "Can't create a project-member because the user doesn't exist");
  }

  @Transactional
  @Test
  void saveWithTeacherContext() {
    given(projectMapper.selectById(eq(mockProject.getId()))).willReturn(mockProject);
    given(pourrfotUserMapper.selectById(eq(mockStudent.getId()))).willReturn(mockStudent);

    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(projectMemberService.save(studentMember));
  }

  @Transactional
  @Test
  void saveWithTeacherContextBecauseNotHisProject() {
    given(projectMapper.selectById(eq(mockProject.getId()))).willReturn(mockProject);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);

    mockTeacherAuthenticationToken.setDetails(Map.of("id", 9999L, "username", "mock",
      "role", "teacher"));
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> projectMemberService.save(teacherMember),
      "Teacher can't create a project-member for project not belong to his/her");
  }

  @Transactional
  @Test
  void updateById() {
    given(projectMapper.selectById(eq(mockProject.getId()))).willReturn(mockProject);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectMemberService.save(teacherMember));

    assertTrue(projectMemberService.updateById(teacherMember.setRoleName("MOCK")));

    // reset
    teacherMember.setRoleName(ProjectMemberRoleEnum.OWNER.name());
  }

  @Transactional
  @Test
  void updateByIdFailedBecauseNotFound() {
    assertThrows(NotFoundException.class, () -> projectMemberService.updateById(teacherMember),
      "Can't update the project-member because the project-member doesn't exist");
  }

  @Transactional
  @Test
  void updateByIdFailedBecauseUpdateImmutableFields() {
    given(projectMapper.selectById(eq(mockProject.getId()))).willReturn(mockProject);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectMemberService.save(teacherMember));

    teacherMember.setProjectId(999);
    assertThrows(IllegalCRUDOperationException.class, () -> projectMemberService.updateById(teacherMember),
      "Can't update the project-member's immutable fields"
    );
    // reset
    teacherMember.setProjectId(mockProject.getId());
  }

  @Transactional
  @Test
  void updateByIdWithTeacherContext() {
    given(projectMapper.selectById(eq(mockProject.getId()))).willReturn(mockProject);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectMemberService.save(teacherMember));

    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(projectMemberService.updateById(teacherMember.setRoleName("MOCK")));

    // reset
    teacherMember.setRoleName(ProjectMemberRoleEnum.OWNER.name());
  }

  @Transactional
  @Test
  void updateByIdWithTeacherContextFailedBecauseNotHisProject() {
    given(projectMapper.selectById(eq(mockProject.getId()))).willReturn(mockProject);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectMemberService.save(teacherMember));

    mockTeacherAuthenticationToken.setDetails(Map.of("id", 999L, "username", "mock",
      "role", "teacher"));
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> projectMemberService.updateById(teacherMember.setRoleName("MOCK")),
      "Teacher can't update a project-member for project not belong to his/her");

    // reset
    teacherMember.setRoleName(ProjectMemberRoleEnum.OWNER.name());
  }

  @Transactional
  @Test
  void removeById() {
    assertFalse(projectMemberService.removeById(100));

    given(projectMapper.selectById(eq(mockProject.getId()))).willReturn(mockProject);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectMemberService.save(teacherMember));
    assertTrue(projectMemberService.removeById(teacherMember.getId()));
  }

  @Transactional
  @Test
  void removeByIdWithTeacherContext() {
    given(projectMapper.selectById(eq(mockProject.getId()))).willReturn(mockProject);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectMemberService.save(teacherMember));

    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(projectMemberService.removeById(teacherMember.getId()));
  }

  @Transactional
  @Test
  void removeByIdWithTeacherContextFailedBecauseNotHisProject() {
    given(projectMapper.selectById(eq(mockProject.getId()))).willReturn(mockProject);
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectMemberService.save(teacherMember));

    mockTeacherAuthenticationToken.setDetails(Map.of("id", 999L, "username", "mock",
      "role", "teacher"));
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> projectMemberService.removeById(teacherMember.getId()),
      "Teacher can't delete a project-member for project not belong to his/her");
  }

  @TestConfiguration
  public static class TestConfig {
    @Bean
    @Primary
    public PourrfotUserMapper pourrfotUserMapper() {
      return Mockito.mock(PourrfotUserMapper.class);
    }

    @Bean
    @Primary
    public ProjectMapper projectMapper() {
      return Mockito.mock(ProjectMapper.class);
    }
  }
}
