/*
 *
 *  * Copyright 2023 VMware, Inc.
 *  * SPDX-License-Identifier: GPL-3.0
 *
 */

package gemfire.showcase.account.web.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Component
@EnableJpaRepositories
@EnableTransactionManagement
public class JobRepositoryConfig implements BeanPostProcessor {

    private String jobRepositorySql = """
                        
            CREATE TABLE IF NOT EXISTS BOOT3_BATCH_JOB_INSTANCE  (
            	JOB_INSTANCE_ID BIGINT  NOT NULL PRIMARY KEY ,
            	VERSION BIGINT ,
            	JOB_NAME VARCHAR(100) NOT NULL,
            	JOB_KEY VARCHAR(32) NOT NULL,
            	constraint BOOT3_JOB_INST_UN unique (JOB_NAME, JOB_KEY)
            ) ;
                        
            -- rollback drop table BOOT3_BATCH_JOB_INSTANCE;
                        
            CREATE TABLE IF NOT EXISTS BOOT3_BATCH_JOB_EXECUTION  (
            	JOB_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
            	VERSION BIGINT  ,
            	JOB_INSTANCE_ID BIGINT NOT NULL,
            	CREATE_TIME TIMESTAMP NOT NULL,
            	START_TIME TIMESTAMP DEFAULT NULL ,
            	END_TIME TIMESTAMP DEFAULT NULL ,
            	STATUS VARCHAR(10) ,
            	EXIT_CODE VARCHAR(2500) ,
            	EXIT_MESSAGE VARCHAR(2500) ,
            	LAST_UPDATED TIMESTAMP,
            	constraint BOOT3_JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
            	references BOOT3_BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
            ) ;
                        
            -- rollback drop table BOOT3_BATCH_JOB_EXECUTION;
                        
            CREATE TABLE IF NOT EXISTS BOOT3_BATCH_JOB_EXECUTION_PARAMS  (
            	JOB_EXECUTION_ID BIGINT NOT NULL ,
            	PARAMETER_NAME VARCHAR(100) NOT NULL ,
            	PARAMETER_TYPE VARCHAR(100) NOT NULL ,
            	PARAMETER_VALUE VARCHAR(2500) ,
            	IDENTIFYING CHAR(1) NOT NULL ,
            	constraint BOOT3_JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
            	references BOOT3_BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
            ) ;
                        
            -- rollback drop table BOOT3_BATCH_JOB_EXECUTION_PARAMS;
                        
            CREATE TABLE IF NOT EXISTS BOOT3_BATCH_STEP_EXECUTION  (
            	STEP_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
            	VERSION BIGINT NOT NULL,
            	STEP_NAME VARCHAR(100) NOT NULL,
            	JOB_EXECUTION_ID BIGINT NOT NULL,
            	CREATE_TIME TIMESTAMP NOT NULL,
            	START_TIME TIMESTAMP DEFAULT NULL ,
            	END_TIME TIMESTAMP DEFAULT NULL ,
            	STATUS VARCHAR(10) ,
            	COMMIT_COUNT BIGINT ,
            	READ_COUNT BIGINT ,
            	FILTER_COUNT BIGINT ,
            	WRITE_COUNT BIGINT ,
            	READ_SKIP_COUNT BIGINT ,
            	WRITE_SKIP_COUNT BIGINT ,
            	PROCESS_SKIP_COUNT BIGINT ,
            	ROLLBACK_COUNT BIGINT ,
            	EXIT_CODE VARCHAR(2500) ,
            	EXIT_MESSAGE VARCHAR(2500) ,
            	LAST_UPDATED TIMESTAMP,
            	constraint BOOT3_JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
            	references BOOT3_BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
            ) ;
                        
            -- rollback drop table BOOT3_BATCH_STEP_EXECUTION;
                        
            CREATE TABLE IF NOT EXISTS BOOT3_BATCH_STEP_EXECUTION_CONTEXT  (
            	STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
            	SHORT_CONTEXT VARCHAR(2500) NOT NULL,
            	SERIALIZED_CONTEXT TEXT ,
            	constraint BOOT3_STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
            	references BOOT3_BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
            ) ;
            -- rollback drop table BOOT3_BATCH_STEP_EXECUTION_CONTEXT;
                        
            CREATE TABLE IF NOT EXISTS BOOT3_BATCH_JOB_EXECUTION_CONTEXT  (
            	JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
            	SHORT_CONTEXT VARCHAR(2500) NOT NULL,
            	SERIALIZED_CONTEXT TEXT ,
            	constraint BOOT3_JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
            	references BOOT3_BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
            ) ;
            -- rollback drop table BOOT3_BATCH_JOB_EXECUTION_CONTEXT;
                        
            CREATE SEQUENCE  IF NOT EXISTS BOOT3_BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
            -- rollback drop sequence BOOT3_BATCH_STEP_EXECUTION_SEQ;
                        
            CREATE SEQUENCE  IF NOT EXISTS BOOT3_BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
            -- rollback drop sequence BOOT3_BATCH_JOB_EXECUTION_SEQ;
                        
            CREATE SEQUENCE  IF NOT EXISTS BOOT3_BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
            -- rollback drop sequence BOOT3_BATCH_JOB_SEQ;
            """;


    @Order(8)
    @ConditionalOnProperty( name= "batch.job.repository.create", havingValue = "true")
    @Bean
    CommandLineRunner setupJobRepository(DataSource dataSource)
    {
        return args -> {

            try (var conn = dataSource.getConnection();
                 var statement = conn.createStatement()) {
                log.info("CREATING jobs repository with DDL {}",jobRepositorySql);
                statement.execute(jobRepositorySql);
            } catch (SQLException e) {
                log.warn("Cannot setup Job Repository {}",e);
            }
        };
    }
}
