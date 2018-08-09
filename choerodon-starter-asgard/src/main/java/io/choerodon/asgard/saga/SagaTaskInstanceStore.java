package io.choerodon.asgard.saga;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class SagaTaskInstanceStore {

    private static final String TASK_INSTANCE_INSERT_STATEMENT = "insert into saga_task_instance_record SET id = ?, create_time = ? on duplicate key update create_time = ?";

    private static final String TASK_INSTANCE_DELETE_STATEMENT = "delete from saga_task_instance_record where id = ?";

    private static final String TASK_INSTANCE_TABLE_EXIST_STATEMENT = "select count(*) from saga_task_instance_record";

    private static final String TASK_INSTANCE_SELECT_OVERTIME_STATEMENT = "select id from saga_task_instance_record";

    private final JdbcTemplate jdbcTemplate;

    private Boolean tableExist = null;

    public SagaTaskInstanceStore(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    void storeTaskInstance(Long id) {
        final long time = System.currentTimeMillis();
        jdbcTemplate.update(TASK_INSTANCE_INSERT_STATEMENT, id, time, time);
    }

    void removeTaskInstance(Long id) {
        jdbcTemplate.update(TASK_INSTANCE_DELETE_STATEMENT, id);
    }

    List<Long> selectOvertimeTaskInstance() {
        return jdbcTemplate.queryForList(TASK_INSTANCE_SELECT_OVERTIME_STATEMENT, Long.class);
    }

    boolean tableNotExist() {
        if (tableExist != null) {
            return tableExist;
        } else {
            try {
                jdbcTemplate.queryForObject(TASK_INSTANCE_TABLE_EXIST_STATEMENT, Integer.class);
                tableExist = false;
            } catch (Exception e) {
                tableExist = true;
            }
            return tableExist;
        }
    }


}
