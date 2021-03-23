/******************************************************************************
 * Copyright (C) 2019 by the ARA Contributors                                 *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * 	 http://www.apache.org/licenses/LICENSE-2.0                               *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 *                                                                            *
 ******************************************************************************/

package com.decathlon.ara.web.rest;


import com.decathlon.ara.Entities;
import com.decathlon.ara.service.ProjectService;
import com.decathlon.ara.scenario.common.service.ScenarioService;
import com.decathlon.ara.service.dto.ignore.ScenarioIgnoreSourceDTO;
import com.decathlon.ara.service.dto.scenario.ScenarioSummaryDTO;
import com.decathlon.ara.service.exception.BadRequestException;
import com.decathlon.ara.service.exception.NotFoundException;
import com.decathlon.ara.scenario.cucumber.upload.CucumberScenarioUploader;
import com.decathlon.ara.scenario.postman.upload.PostmanScenarioUploader;
import com.decathlon.ara.web.rest.util.HeaderUtil;
import com.decathlon.ara.web.rest.util.ResponseUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.decathlon.ara.web.rest.util.RestConstants.PROJECT_API_PATH;

/**
 * REST controller for managing Scenarios.
 */
@Slf4j
@RestController
@RequestMapping(ScenarioResource.PATH)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScenarioResource {

    private static final String NAME = Entities.SCENARIO;
    static final String PATH = PROJECT_API_PATH + "/" + NAME + "s";

    @NonNull
    private final ScenarioService scenarioService;

    @NonNull
    private final PostmanScenarioUploader postmanScenarioUploader;

    @NonNull
    private final CucumberScenarioUploader cucumberScenarioUploader;

    @NonNull
    private final ProjectService projectService;

    /**
     * POST to move upload the scenario set of a test code.
     *
     * @param projectCode the code of the project in which to work
     * @param sourceCode  the source-code determining the location of the files that are uploaded
     * @param json        the report.json file as generated by a cucumber --dry-run
     * @return OK on success, INTERNAL_SERVER_ERROR on processing error
     */
    @Deprecated
    @PostMapping("/upload/{sourceCode}")
    public ResponseEntity<Void> uploadCucumber(@PathVariable String projectCode, @PathVariable String sourceCode, @Valid @RequestBody String json) {
        log.warn("Beware! This resource (scenarios/upload/{sourceCode}) is deprecated.");
        log.warn("Please, call the new resource instead: cucumber/scenarios/upload/{sourceCode}");
        try {
            cucumberScenarioUploader.uploadCucumber(projectService.toId(projectCode), sourceCode, json);
            return ResponseEntity.ok().build();
        } catch (BadRequestException e) {
            log.error("Failed to upload Cucumber scenarios for source code {}", sourceCode, e);
            return ResponseUtil.handle(e);
        }
    }

    @Deprecated
    @PostMapping("/upload-postman/{sourceCode}")
    public ResponseEntity<Void> uploadPostman(@PathVariable String projectCode,
                                              @PathVariable String sourceCode,
                                              @RequestParam("file") MultipartFile file) {
        log.warn("Beware! This resource (scenarios/upload-postman/{sourceCode}) is deprecated.");
        log.warn("Please, call the new resource instead: postman/scenarios/upload/{sourceCode}");
        File tempZipFile = null;
        try {
            tempZipFile = File.createTempFile("ara_scenario_upload_", ".zip");
            tempZipFile.deleteOnExit();
            file.transferTo(tempZipFile);
            postmanScenarioUploader.uploadPostman(projectService.toId(projectCode), sourceCode, tempZipFile);
            return ResponseEntity.ok().build();
        } catch (BadRequestException e) {
            log.error("Failed to upload ZIP file containing Postman requests for source code {}", sourceCode, e);
            return ResponseUtil.handle(e);
        } catch (IOException e) {
            log.error("Failed to upload ZIP file containing Postman requests for source code {}", sourceCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(HeaderUtil.exception(Entities.SCENARIO, e))
                    .build();
        } finally {
            FileUtils.deleteQuietly(tempZipFile);
        }
    }

    /**
     * @param projectCode the code of the project in which to work
     * @return all scenarios that have no associated functionalities or have wrong or nonexistent functionality identifier
     */
    @GetMapping("/without-functionalities")
    public ResponseEntity<List<ScenarioSummaryDTO>> getAllWithFunctionalityErrors(@PathVariable String projectCode) {
        try {
            return ResponseEntity.ok().body(scenarioService.findAllWithFunctionalityErrors(projectService.toId(projectCode)));
        } catch (NotFoundException e) {
            return ResponseUtil.handle(e);
        }
    }

    /**
     * @param projectCode the code of the project in which to work
     * @return for each source (API, Web...), a count of ignored&amp;total scenarios and a list of ignored scenarios by feature file
     */
    @GetMapping("/ignored")
    public ResponseEntity<List<ScenarioIgnoreSourceDTO>> getIgnoredScenarioCounts(@PathVariable String projectCode) {
        try {
            return ResponseEntity.ok().body(scenarioService.getIgnoredScenarioCounts(projectService.toId(projectCode)));
        } catch (NotFoundException e) {
            return ResponseUtil.handle(e);
        }
    }

}
