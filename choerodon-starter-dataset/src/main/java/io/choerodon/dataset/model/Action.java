package io.choerodon.dataset.model;

import io.choerodon.dataset.exception.DatasetException;
import org.apache.ibatis.session.SqlSession;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * @author xausky
 */
public abstract class Action {
    private String prefilter = null;
    private String postfilter = null;

    public enum Type {
        SELECT, INSERT, UPDATE, DELETE, LANGUAGES, VEILDATA
    }

    public void setPrefilter(String prefilter) {
        this.prefilter = prefilter;
    }

    public void setPostfilter(String postfilter) {
        this.postfilter = postfilter;
    }

    public Object process(ExpressionParser parser, StandardEvaluationContext context,
                          SqlSession session, Map<String, Object> parameter) throws DatasetException{
        Object result;
        if(prefilter != null){
            context.setVariable("request", parameter);
            if(Boolean.FALSE.equals(parser.parseExpression(prefilter).getValue(context, Boolean.class))){
                throw new DatasetException("dataset.prefilter.abort", null);
            }
        }
        result = invoke(session, parameter);
        if(postfilter != null){
            context.setVariable("request", parameter);
            if(Boolean.FALSE.equals(parser.parseExpression(postfilter).getValue(context, Boolean.class))){
                throw new DatasetException("dataset.postfilter.abort", null);
            }
        }
        return result;
    }

    /**
     * 执行方法
     * @param session
     * @param parameter
     * @return
     */
    protected abstract Object invoke(SqlSession session, Map<String, Object> parameter) throws DatasetException;
}