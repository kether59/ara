package com.decathlon.ara.scenario.cucumber.upload;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.decathlon.ara.Entities;
import com.decathlon.ara.domain.enumeration.Technology;
import com.decathlon.ara.scenario.common.upload.ScenarioUploader;
import com.decathlon.ara.scenario.cucumber.bean.Feature;
import com.decathlon.ara.scenario.cucumber.util.CucumberReportUtil;
import com.decathlon.ara.scenario.cucumber.util.ScenarioExtractorUtil;
import com.decathlon.ara.service.exception.BadRequestException;

@Component
public class CucumberScenarioUploader {

    private static final Logger LOG = LoggerFactory.getLogger(CucumberScenarioUploader.class);

    private final ScenarioUploader uploader;

    public CucumberScenarioUploader(ScenarioUploader uploader) {
        this.uploader = uploader;
    }

    /**
     * Upload the Cucumber scenario set of a test type.
     *
     * @param projectId  the ID of the project in which to work
     * @param sourceCode the source-code determining the location of the files that are uploaded
     * @param json       the report.json file as generated by a cucumber --dry-run
     * @throws BadRequestException if the source cannot be found, the source code is not using CUCUMBER technology, or something goes wrong while parsing the report content
     */
    public void uploadCucumber(long projectId, String sourceCode, String json) throws BadRequestException {
        LOG.info("SCENARIO|Preparing for Cucumber scenarios to upload");
        LOG.debug("SCENARIO|Receiving the following json:");
        LOG.debug(json);
        uploader.processUploadedContent(projectId, sourceCode, Technology.CUCUMBER, source -> {
            // Extract and save scenarios of the source from the report.json
            List<Feature> features;
            try {
                LOG.info("SCENARIO|Preparing to parse the Cucumber JSON report");
                features = CucumberReportUtil.parseReportJson(json);
                LOG.debug("SCENARIO|Extracting {} Cucumber features", features.size());
            } catch (IOException e) {
                LOG.error("SCENARIO|Cannot parse uploaded Cucumber report.json", e);
                throw new BadRequestException("Cannot parse uploaded Cucumber report.json", Entities.SCENARIO, "cannot_parse_report_json");
            }
            return ScenarioExtractorUtil.extractScenarios(source, features);
        });
    }
}
