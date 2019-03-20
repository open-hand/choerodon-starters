/**
 * Copyright 2016 www.extdo.com 
 */
package io.choerodon.mybatis.common.query;

/**
 * @author njq.niu@hand-china.com
 */
public class Selection extends SQLField {

    private String expression;

    public Selection(String field) {
        this(field, null);
    }

    public Selection(String field, String expression) {
        super(field);
        setExpression(expression);
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

//    @Override
//    public String sql(BaseDTO dto) {
//        EntityColumn entityColumn = findColumn(dto.getClass());
//        StringBuilder sb = new StringBuilder();
//        if (entityColumn != null) {
//            EntityTable table = EntityHelper.getEntityTable(dto.getClass());
//            JoinColumn jc = entityColumn.getJoinColumn();
//            if (jc != null) {
//                EntityColumn joinField = table.getJoinMapping().get(jc.joinName());
//                if (joinField != null && joinField.getJoinTable() != null) {
//                    EntityTable joinTable = EntityHelper.getEntityTable(joinField.getJoinTable().target());
//                    EntityColumn refColumn = joinTable.findColumnByProperty(jc.field());
//                    sb.append(table.getAlias(joinField.getJoinKey())).append(".").append(refColumn.getColumn()).append(" AS ").append(entityColumn.getColumn());
//                }
//            } else {
//                sb.append(table.getAlias()).append(".").append(entityColumn.getColumn());
//            }
//        }
//        return sb.toString();
//    }

}
