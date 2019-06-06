package io.choerodon.actuator.dataset;

import io.choerodon.actuator.dataset.domain.ActionJoinTable;
import io.choerodon.actuator.dataset.domain.ActionProperty;
import io.choerodon.actuator.dataset.domain.DatabasePageAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Iterator;

@Service
public class MysqlExecutor implements DatabaseActionExecutor{
    @Autowired
    private JdbcTemplate template;

    @Override
    public void process(DatabasePageAction action) {
        StringBuilder sql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        sql.append("SELECT ");

        countSql.append("SELECT COUNT(*) FROM ");
        Iterator<ActionProperty> propertyIterator = action.getProperties().iterator();
        while (propertyIterator.hasNext()){
            ActionProperty property = propertyIterator.next();
            sql.append(property.getTable().getSchema());
            sql.append('.');
            sql.append(property.getTable().getName());
            sql.append('.');
            sql.append(property.getColumn());
            sql.append(" AS \"");
            sql.append(property.getName());
            sql.append('\"');
            if (propertyIterator.hasNext()){
                sql.append(',');
            }
        }
        sql.append(" FROM ");

        sql.append(action.getMasterActionTable().getSchema());
        sql.append('.');
        sql.append(action.getMasterActionTable().getName());
        for (ActionJoinTable actionJoinTable : action.getJoinTables()){
            sql.append(" LEFT JOIN ");
            sql.append(actionJoinTable.getJoinTable().getSchema());
            sql.append('.');
            sql.append(actionJoinTable.getJoinTable().getName());
            sql.append(" ON (");
            sql.append(actionJoinTable.getJoinTable().getSchema());
            sql.append('.');
            sql.append(actionJoinTable.getJoinTable().getName());
            sql.append('.');
            sql.append(actionJoinTable.getJoinColumn());
            sql.append('=');
            sql.append(action.getMasterActionTable().getSchema());
            sql.append('.');
            sql.append(action.getMasterActionTable().getName());
            sql.append('.');
            sql.append(actionJoinTable.getMasterColumn());
            sql.append(") ");
        }
        countSql.append(action.getMasterActionTable().getSchema());
        countSql.append('.');
        countSql.append(action.getMasterActionTable().getName());
        sql.append(" LIMIT ?,?");

        action.setCount(template.queryForObject(countSql.toString(), Long.class));
        if (action.getCount() == null){
            throw new RuntimeException("Count sql result null :" + countSql.toString());
        }
        if (action.getCount() > 0){
            action.setResult(template.queryForList(sql.toString(), (action.getPage() - 1) * action.getPageSize(), action.getPageSize()));
        } else {
            action.setResult(Collections.emptyList());
        }
    }
}
