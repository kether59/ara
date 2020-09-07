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

package com.decathlon.ara.configuration;

import javax.sql.DataSource;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.retry.annotation.EnableRetry;

/**
 * When starting ARA server at the same time as the database (with eg. Docker Compose), make sure the application waits
 * for a live database connection during a few seconds before failing to start up.
 */
@Configuration
@EnableRetry
@ComponentScan("com.decathlon.ara.domain")
@ComponentScan("com.decathlon.ara.repository")
@ComponentScan("com.decathlon.ara.configuration")
public class DataSourceConfiguration {

    @Bean
    public BeanPostProcessor dataSourceWrapper() {
        return new RetryableDataSourceBeanPostProcessor();
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    private class RetryableDataSourceBeanPostProcessor implements BeanPostProcessor {

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            if (bean instanceof DataSource) {
                bean = new RetryableDataSource((DataSource) bean);
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            return bean;
        }

    }

}
