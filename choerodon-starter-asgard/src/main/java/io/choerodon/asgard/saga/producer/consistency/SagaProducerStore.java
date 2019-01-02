package io.choerodon.asgard.saga.producer.consistency;

import io.choerodon.asgard.saga.exception.SagaProducerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class SagaProducerStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaProducerStore.class);

    private static final String INSERT_STATEMENT = "insert into asgard_producer_record SET uuid = ?, create_time = ?";

    private static final String DELETE_STATEMENT = "delete from asgard_producer_record where create_time + ? < ?";

    private static final String SELECT_STATEMENT = "select uuid from asgard_producer_record where uuid = ?";

    private final JdbcTemplate jdbcTemplate;

    public SagaProducerStore(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    void record(String uuid) {
        int row = jdbcTemplate.update(INSERT_STATEMENT, uuid, System.currentTimeMillis());
        if (row != 1) {
            throw new SagaProducerException("error.saga.start.recordUUID");
        }
        LOGGER.debug("insert into asgard_producer_record,  UUID: {}", uuid);
    }


    void clear(long time) {
        int row = jdbcTemplate.update(DELETE_STATEMENT, time, System.currentTimeMillis());
        LOGGER.info("clear asgard_producer_record createTime before: {} , number of deleted rows is: {}", time, row);
    }

    String selectByUUID(String uuid) {
        return jdbcTemplate.queryForObject(SELECT_STATEMENT, new Object[]{uuid}, String.class);
    }
}
