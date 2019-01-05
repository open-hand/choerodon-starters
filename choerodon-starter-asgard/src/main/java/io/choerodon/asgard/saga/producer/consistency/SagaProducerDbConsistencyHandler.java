package io.choerodon.asgard.saga.producer.consistency;

import io.choerodon.asgard.saga.dto.SagaStatusQueryDTO;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.exception.SagaProducerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import static io.choerodon.asgard.saga.dto.SagaStatusQueryDTO.STATUS_CANCEL;
import static io.choerodon.asgard.saga.dto.SagaStatusQueryDTO.STATUS_CONFIRM;

/**
 * 基于数据表实现的回查方式
 */
public class SagaProducerDbConsistencyHandler extends SagaProducerConsistencyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaProducerDbConsistencyHandler.class);

    private static final String INSERT_STATEMENT = "insert into asgard_producer_record SET uuid = ?, payload = ?, ref_type = ?, ref_id = ?, create_time = ?";

    private static final String DELETE_STATEMENT = "delete from asgard_producer_record where create_time + ? < ?";

    private static final String SELECT_STATEMENT = "select payload,ref_type,ref_id  from asgard_producer_record where uuid = ?";

    private final JdbcTemplate jdbcTemplate;

    public SagaProducerDbConsistencyHandler(ScheduledExecutorService executorService, DataSource dataSource) {
        super(executorService);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void beforeTransactionCommit(String uuid, StartInstanceDTO dto) {
        int row = jdbcTemplate.update(INSERT_STATEMENT, uuid, dto.getInput(), dto.getRefType(), dto.getRefId(), System.currentTimeMillis());
        if (row != 1) {
            throw new SagaProducerException("error.saga.start.record");
        }
        LOGGER.trace("insert into asgard_producer_record,  UUID: {}", uuid);
    }

    @Override
    public void beforeTransactionCancel(String uuid) {
        // do nothing
    }

    @Override
    public SagaStatusQueryDTO asgardServiceBackCheck(String uuid) {
        Map<String, Object> value = jdbcTemplate.queryForMap(SELECT_STATEMENT, uuid);
        if (value == null) {
            return new SagaStatusQueryDTO(STATUS_CANCEL);
        } else {
            return new SagaStatusQueryDTO(STATUS_CONFIRM, (String) value.get("payload"), (String) value.get("ref_type"), (String) value.get("ref_id"));
        }
    }

    @Override
    public void clear(long time) {
        int row = jdbcTemplate.update(DELETE_STATEMENT, time, System.currentTimeMillis());
        if (row >0) {
            LOGGER.info("clear asgard_producer_record createTime before: {} , number of deleted rows is: {}", time, row);
        }
    }

}
