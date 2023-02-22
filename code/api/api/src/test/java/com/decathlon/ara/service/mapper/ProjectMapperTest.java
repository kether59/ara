package com.decathlon.ara.service.mapper;

import com.decathlon.ara.domain.Project;
import com.decathlon.ara.domain.security.member.user.account.User;
import com.decathlon.ara.service.dto.project.ProjectDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectMapperTest {

    @InjectMocks
    private ProjectMapper projectMapper;

    @Test
    void getProjectDTOFromProjectEntity_returnProjectDTO_whenProjectIsNotNull() {
        // Given
        var projectToConvert = mock(Project.class);
        var projectId = 1L;
        var projectCode = "project-code";
        var projectName = "project-name";
        var projectDescription = "A small description of this project";

        var creationDate = ZonedDateTime.now().minusDays(3);
        var creationUser = mock(User.class);
        var creationUserLogin = "creation-user-login";

        var updateDate = ZonedDateTime.now();
        var updateUser = mock(User.class);
        var updateUserLogin = "update-user-login";

        // When
        when(projectToConvert.getId()).thenReturn(projectId);
        when(projectToConvert.getCode()).thenReturn(projectCode);
        when(projectToConvert.getName()).thenReturn(projectName);
        when(projectToConvert.getDescription()).thenReturn(projectDescription);
        when(projectToConvert.getCreationDate()).thenReturn(creationDate);
        when(projectToConvert.getCreationUser()).thenReturn(creationUser);
        when(projectToConvert.getUpdateDate()).thenReturn(updateDate);
        when(projectToConvert.getUpdateUser()).thenReturn(updateUser);

        when(creationUser.getLogin()).thenReturn(creationUserLogin);
        when(updateUser.getLogin()).thenReturn(updateUserLogin);

        // Then
        var convertedProject = projectMapper.getProjectDTOFromProject(projectToConvert);
        assertThat(convertedProject)
                .extracting(
                        "id",
                        "name",
                        "code",
                        "description",
                        "creationDate",
                        "creationUserLogin",
                        "updateDate",
                        "updateUserLogin"
                )
                .containsExactly(
                        projectId,
                        projectName,
                        projectCode,
                        projectDescription,
                        Date.from(creationDate.toInstant()),
                        creationUserLogin,
                        Date.from(updateDate.toInstant()),
                        updateUserLogin
                );
    }

    @Test
    void getProjectEntityFromProjectDTO_returnProject_whenProjectDTOIsNotNull() {
        // Given
        var projectToConvert = mock(ProjectDTO.class);
        var projectId = 1L;
        var projectCode = "project-code";
        var projectName = "project-name";
        var projectDescription = "A small description of this project";

        // When
        when(projectToConvert.getId()).thenReturn(projectId);
        when(projectToConvert.getCode()).thenReturn(projectCode);
        when(projectToConvert.getName()).thenReturn(projectName);
        when(projectToConvert.getDescription()).thenReturn(projectDescription);

        // Then
        var convertedProject = projectMapper.getProjectFromProjectDTO(projectToConvert);
        assertThat(convertedProject)
                .extracting(
                        "id",
                        "name",
                        "code",
                        "description"
                )
                .containsExactly(
                        projectId,
                        projectName,
                        projectCode,
                        projectDescription
                );
        var now = ZonedDateTime.now();
        assertThat(convertedProject.getCreationDate()).isBetween(now.minusSeconds(1), now.plusSeconds(1));
    }
}