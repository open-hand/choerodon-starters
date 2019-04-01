package io.choerodon.dataset

import io.choerodon.dataset.service.IDatasetRepositoryService
import io.choerodon.liquibase.LiquibaseConfig
import io.choerodon.liquibase.LiquibaseExecutor
import io.choerodon.mybatis.ChoerodonMybatisAutoConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification

import javax.annotation.PostConstruct

@ComponentScan
@Import([LiquibaseConfig, ChoerodonMybatisAutoConfiguration])
@SpringBootTest(classes = [TestApplication])
class DatasetSpec extends Specification {

    @Autowired
    MockMvc mvc;

    @Autowired
    LiquibaseExecutor liquibaseExecutor;

    @Autowired
    IDatasetRepositoryService datasetRepositoryService;

    @PostConstruct
    void init() {
        liquibaseExecutor.execute()
    }

    def "Dataset Controller Query" () {
        mvc.perform(MockMvcRequestBuilders.post("/dataset/test/queries")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("status").value("success"))
    }

}
