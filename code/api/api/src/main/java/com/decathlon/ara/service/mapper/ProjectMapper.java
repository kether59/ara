package com.decathlon.ara.service.mapper;

import com.decathlon.ara.domain.Project;
import com.decathlon.ara.service.dto.project.ProjectDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ProjectMapper {

    public ProjectDTO getProjectDTOFromProjectEntity(@NonNull Project project) {
        var id = project.getId();
        var code = project.getCode();
        var name = project.getName();
        var projectToConvert = new ProjectDTO(id, code, name);

        var creationDate = project.getCreationDate();
        if (creationDate != null) {
            projectToConvert.setCreationDate(Date.from(creationDate.toInstant()));
        }
        var creationUser = project.getCreationUser();
        if (creationUser != null) {
            projectToConvert.setCreationUserLogin(creationUser.getLogin());
        }

        var updateDate = project.getUpdateDate();
        if (updateDate != null) {
            projectToConvert.setUpdateDate(Date.from(updateDate.toInstant()));
        }
        var updateUser = project.getUpdateUser();
        if (updateUser != null) {
            projectToConvert.setUpdateUserLogin(updateUser.getLogin());
        }
        return projectToConvert;
    }

    public Project getProjectEntityFromProjectDTO(@NonNull ProjectDTO projectDTO) {
        var id = projectDTO.getId();
        var code = projectDTO.getCode();
        var name = projectDTO.getName();
        return new Project(id, code, name);
    }
}
