package cn.edu.shu.pourrfot.server.service.impl;

import cn.edu.shu.pourrfot.server.config.CustomMySQLContainer;
import cn.edu.shu.pourrfot.server.enums.RoleEnum;
import cn.edu.shu.pourrfot.server.exception.IllegalCRUDOperationException;
import cn.edu.shu.pourrfot.server.exception.NotFoundException;
import cn.edu.shu.pourrfot.server.filter.JwtAuthorizationFilter;
import cn.edu.shu.pourrfot.server.model.PourrfotUser;
import cn.edu.shu.pourrfot.server.model.Project;
import cn.edu.shu.pourrfot.server.repository.PourrfotUserMapper;
import cn.edu.shu.pourrfot.server.repository.ProjectMemberMapper;
import cn.edu.shu.pourrfot.server.service.ProjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import static cn.edu.shu.pourrfot.server.enums.ProjectMemberRoleEnum.MEMBER;
import static cn.edu.shu.pourrfot.server.model.ProjectMember.builder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Slf4j
@Testcontainers(disabledWithoutDocker = true)
class ProjectServiceImplTest {
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
    .build();
  @Autowired
  private ProjectService projectService;
  @Autowired
  private PourrfotUserMapper pourrfotUserMapper;
  @Autowired
  private ProjectMemberMapper projectMemberMapper;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.getContext().setAuthentication(null);
  }

  @Transactional
  @Test
  void pageIntegrationTest() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectService.save(mockProject));

    assertEquals(1, projectService.page(new Page<>(1, 10), new QueryWrapper<>(new Project())).getRecords().size());
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertEquals(1, projectService.page(new Page<>(1, 10), new QueryWrapper<>(new Project())).getRecords().size());


    assertEquals(1, projectMemberMapper.insert(builder()
      .projectId(mockProject.getId())
      .userId(mockStudent.getId())
      .roleName(MEMBER.name())
      .build()));
    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertEquals(1, projectService.page(new Page<>(1, 10), new QueryWrapper<>(new Project())).getRecords().size());
  }

  @Transactional
  @Test
  void getById() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectService.save(mockProject));
    assertNotNull(projectService.getById(mockProject.getId()));
  }

  @Transactional
  @Test
  void getByIdWithTeacherContext() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectService.save(mockProject));
    assertNotNull(projectService.getById(mockProject.getId()));

    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertNotNull(projectService.getById(mockProject.getId()));
  }

  @Transactional
  @Test
  void getByIdWithStudentContextFailedBecauseNotInvolved() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectService.save(mockProject));
    assertNotNull(projectService.getById(mockProject.getId()));

    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertThrows(IllegalCRUDOperationException.class, () -> projectService.getById(mockProject.getId()),
      "User can't access the project not belong to his/her");
  }

  @Transactional
  @Test
  void getByIdWithStudentContext() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectService.save(mockProject));
    assertNotNull(projectService.getById(mockProject.getId()));

    assertEquals(1, projectMemberMapper.insert(builder()
      .projectId(mockProject.getId())
      .userId(mockStudent.getId())
      .roleName(MEMBER.name())
      .build()));

    mockStudentAuthenticationToken.setDetails(studentDetail);
    SecurityContextHolder.getContext().setAuthentication(mockStudentAuthenticationToken);
    assertNotNull(projectService.getById(mockProject.getId()));
  }

  @Transactional
  @Test
  void save() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectService.save(mockProject));
  }

  @Transactional
  @Test
  void saveWithAdminContext() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    assertTrue(projectService.save(mockProject));
  }

  @Transactional
  @Test
  void saveWithAdminContextFailedBecauseOwnerNotExisted() {
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    mockProject.setOwnerId(10000);
    assertThrows(NotFoundException.class, () -> projectService.save(mockProject),
      "Can't create a project because the owner doesn't exist");
    // reset
    mockProject.setOwnerId(teacher.getId());
  }

  @Transactional
  @Test
  void saveWithAdminContextFailedBecauseOwnerHaveNotToBeStudent() {
    given(pourrfotUserMapper.selectById(eq(999))).willReturn(PourrfotUser.builder()
      .id(999)
      .password("student")
      .nickname("student")
      .username("student")
      .role(RoleEnum.student)
      .build());
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    mockProject.setOwnerId(999);
    assertThrows(IllegalCRUDOperationException.class, () -> projectService.save(mockProject),
      "Student can't own a project");
    // reset
    mockProject.setOwnerId(teacher.getId());
  }

  @Transactional
  @Test
  void saveWithTeacherContext() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(projectService.save(mockProject));
  }

  @Transactional
  @Test
  void updateById() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectService.save(mockProject));
    mockProject.setProjectName("UPDATE");
    assertTrue(projectService.updateById(mockProject));
    // reset
    mockProject.setProjectName("MOCK");
  }

  @Transactional
  @Test
  void updateByIdFailedBecauseNotFound() {
    assertThrows(NotFoundException.class, () -> projectService.updateById(mockProject),
      "Can't update the project because the project doesn't exist");
  }

  @Transactional
  @Test
  void updateByIdWithAdmin() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    assertTrue(projectService.save(mockProject));
    mockProject.setProjectName("UPDATE");
    assertTrue(projectService.updateById(mockProject));
    // reset
    mockProject.setProjectName("MOCK");
  }

  @Transactional
  @Test
  void updateByIdWithAdminFailedBecauseUpdateImmutableFields() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    mockAdminAuthenticationToken.setDetails(adminDetail);
    SecurityContextHolder.getContext().setAuthentication(mockAdminAuthenticationToken);
    assertTrue(projectService.save(mockProject));
    mockProject.setOwnerId(999);
    assertThrows(IllegalCRUDOperationException.class, () -> projectService.updateById(mockProject),
      "Can't modify the project's owner");
    // reset
    mockProject.setOwnerId(teacher.getId());
  }

  @Transactional
  @Test
  void updateByIdWithTeacherContext() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectService.save(mockProject));

    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    mockProject.setProjectName("UPDATE");
    assertTrue(projectService.updateById(mockProject));
    // reset
    mockProject.setProjectName("MOCK");
  }

  @Transactional
  @Test
  void removeById() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    assertTrue(projectService.save(mockProject));

    assertTrue(projectService.removeById(mockProject.getId()));
  }

  @Transactional
  @Test
  void removeByIdFailedBecauseNotFound() {
    assertFalse(projectService.removeById(mockProject.getId()));
  }

  @Transactional
  @Test
  void removeByIdWithTeacherContext() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(projectService.save(mockProject));

    assertTrue(projectService.removeById(mockProject.getId()));
  }

  @Transactional
  @Test
  void removeByIdWithTeacherContextFailedBecauseNotOwnProject() {
    given(pourrfotUserMapper.selectById(eq(teacher.getId()))).willReturn(teacher);
    mockTeacherAuthenticationToken.setDetails(teacherDetail);
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);
    assertTrue(projectService.save(mockProject));

    mockTeacherAuthenticationToken.setDetails(Map.of("id", 999L, "username", "mock",
      "role", "teacher"));
    SecurityContextHolder.getContext().setAuthentication(mockTeacherAuthenticationToken);

    assertThrows(IllegalCRUDOperationException.class, () -> projectService.removeById(mockProject.getId()),
      "Teacher user can't delete a not-own project");
  }

  @TestConfiguration
  public static class TestConfig {
    @Bean
    @Primary
    public PourrfotUserMapper pourrfotUserMapper() {
      return Mockito.mock(PourrfotUserMapper.class);
    }
  }
}
