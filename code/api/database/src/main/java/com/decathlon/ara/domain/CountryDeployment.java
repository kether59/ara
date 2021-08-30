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

package com.decathlon.ara.domain;

import com.decathlon.ara.domain.enumeration.JobStatus;
import com.decathlon.ara.domain.enumeration.Result;
import lombok.*;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

import static java.util.Comparator.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
@Entity
// Keep business key in sync with compareTo(): see https://developer.jboss.org/wiki/EqualsAndHashCode
@EqualsAndHashCode(of = { "executionId", "country" })
public class CountryDeployment implements Comparable<CountryDeployment> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_deployment_id")
    @SequenceGenerator(name = "country_deployment_id", sequenceName = "country_deployment_id", allocationSize = 1)
    private Long id;

    // 1/2 for @EqualsAndHashCode to work: used when an entity is fetched by JPA
    @Column(name = "execution_id", insertable = false, updatable = false)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Long executionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "execution_id")
    private Execution execution;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id")
    private Country country;

    /**
     * The platform/environment/server on which this country was deployed.
     */
    private String platform;

    /**
     * The URL of the Continuous Integration job, visible in the client GUI to access logs of the job.
     */
    @Column(length = 512)
    private String jobUrl;

    /**
     * An alternate URL for the job, only for internal indexing needs (optional: either the local directory from which
     * to index or an intermediary service used to eg. compute the Continuous Integration job's hierarchy).
     */
    private String jobLink;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    /**
     * The result status of the remote job.
     */
    @Enumerated(EnumType.STRING)
    private Result result;

    /**
     * The date and time the remote job started. Null if not started yet.
     */
    @Column(name = "start_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDateTime;

    /**
     * The estimated duration of the remote job, in milliseconds: can be used with startDateTime and the current
     * date-time to display a progress bar.
     */
    private Long estimatedDuration;

    /**
     * The actual duration of the job, in milliseconds, AFTER it has finished (may be 0 while running).
     */
    private Long duration;

    // 2/2 for @EqualsAndHashCode to work: used for entities created outside of JPA
    public void setExecution(Execution execution) {
        this.execution = execution;
        this.executionId = (execution == null ? null : execution.getId());
    }

    @Override
    public int compareTo(CountryDeployment other) {
        // Keep business key in sync with @EqualsAndHashCode
        Comparator<CountryDeployment> executionIdComparator = comparing(d -> d.executionId, nullsFirst(naturalOrder()));
        Comparator<CountryDeployment> countryComparator = comparing(CountryDeployment::getCountry, nullsFirst(naturalOrder()));
        return nullsFirst(executionIdComparator
                .thenComparing(countryComparator)).compare(this, other);
    }

}