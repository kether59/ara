/******************************************************************************
 * Copyright (C) 2020 by the ARA Contributors                                 *
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

import com.decathlon.ara.domain.enumeration.Technology;
import lombok.*;

import javax.persistence.*;
import java.util.Comparator;

import static java.util.Comparator.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
@Entity
@EqualsAndHashCode(of = { "projectId", "code", "technology" })
public class TechnologySetting implements Comparable<TechnologySetting> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "technology_setting_id")
    @SequenceGenerator(name = "technology_setting_id", sequenceName = "technology_setting_id", allocationSize = 1)
    private Long id;

    private Long projectId;

    private String code;

    private String value;

    @Enumerated(EnumType.STRING)
    private Technology technology;

    @Override
    public int compareTo(TechnologySetting other) {
        Comparator<TechnologySetting> projectIdComparator = comparing(e -> Long.valueOf(e.projectId), nullsFirst(naturalOrder()));
        Comparator<TechnologySetting> technologyComparator = comparing(e -> e.technology, nullsFirst(naturalOrder()));
        Comparator<TechnologySetting> codeComparator = comparing(e -> e.code, nullsFirst(naturalOrder()));
        return nullsFirst(projectIdComparator
                .thenComparing(technologyComparator)
                .thenComparing(codeComparator)).compare(this, other);
    }
}