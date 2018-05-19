package io.choerodon.mybatis;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanAccessor;


/**
 * Created by jiatong.li on 4/19/17.
 */

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class})})
public class ZipkinInterceptor implements Interceptor {
    private static Logger logger = LoggerFactory.getLogger(ZipkinInterceptor.class);


    private SpanAccessor spanAccessor;

    public ZipkinInterceptor(SpanAccessor spanAccessor) {
        this.spanAccessor = spanAccessor;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object returnValue = null;
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        String sqlId = mappedStatement.getId();
        long start = System.currentTimeMillis();
        returnValue = invocation.proceed();
        long end = System.currentTimeMillis();
        long time = (end - start);
        if (spanAccessor == null || spanAccessor.getCurrentSpan() == null) {
            return returnValue;
        }
        try {
            BoundSql boundSql = mappedStatement.getBoundSql(invocation.getArgs()[1]);
            String sql = boundSql.getSql();
            Span span = spanAccessor.getCurrentSpan();
            StringBuilder builder = new StringBuilder(sql).append("  :").append(time).append("ms");
            span.tag(sqlId, builder.toString());
        } catch (Exception e) {
            logger.warn("ZipkinInterceptor error: {}", e);
        }
        return returnValue;
    }

    @Override
    public Object plugin(Object object) {
        return Plugin.wrap(object, this);
    }

    @Override
    public void setProperties(Properties properties) {
      // no need setProperties
    }
}
