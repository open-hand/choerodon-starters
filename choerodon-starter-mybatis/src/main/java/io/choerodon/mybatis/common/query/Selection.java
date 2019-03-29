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

}
