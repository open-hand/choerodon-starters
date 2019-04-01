package io.choerodon.dataset.model.impl;

import io.choerodon.dataset.model.Action;
import org.apache.ibatis.session.SqlSession;
import org.w3c.dom.Element;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Map;

/**
 * @author xausky
 */
public class MybatisMapperAction extends Action {
    private String statement;
    private Type type;
    private String sortColumn;
    private boolean desc;

    public MybatisMapperAction(String statement, Type type, Element e) {
        setPrefilter(e.getAttribute("prefilter").isEmpty() ? null : e.getAttribute("prefilter"));
        setPostfilter(e.getAttribute("postfilter").isEmpty() ? null : e.getAttribute("postfilter"));
        this.sortColumn = e.getAttribute("sort");
        this.desc = e.getAttribute("desc").equals("true");
        this.type = type;
        this.statement = statement;
    }

    @Override
    public Object invoke(SqlSession session, Map<String, Object> parameter) {
        switch (this.type) {
            case SELECT:
                String sort = (String) parameter.get("__sort");
                Boolean desc = parameter.get("__order") == null ? null : "DESC".equals(parameter.get("__order"));
                if (sort == null) {
                    sort = sortColumn;
                }
                if (desc == null) {
                    desc = this.desc;
                }
                if (!sort.isEmpty()) {
                    parameter.put("__sort_column", StringUtil.camelhumpToUnderline(sort).toUpperCase());
                    parameter.put("__order_flag", desc ? "DESC" : "ASC");
                }
                return session.selectList(statement, parameter);
            case UPDATE:
                session.update(statement, parameter);
                return parameter;
            case INSERT:
                session.insert(statement, parameter);
                return parameter;
            case DELETE:
                session.delete(statement, parameter);
                return parameter;
            default:
        }
        return null;
    }
}
