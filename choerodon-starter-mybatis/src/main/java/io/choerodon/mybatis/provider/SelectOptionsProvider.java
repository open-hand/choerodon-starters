/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.choerodon.mybatis.provider;

import io.choerodon.mybatis.common.SelectOptionsMapper;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.mybatis.mapperhelper.CustomHelper;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;

import java.util.Map;

/**
 * BaseSelectProvider实现类，基础方法实现类
 *
 * @author liuzh
 */
public class SelectOptionsProvider extends MapperTemplate {

    public SelectOptionsProvider() {
        super(null, null);
    }

    public SelectOptionsProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    private void initResultType(MappedStatement ms){
        Class<?> entityClass = getEntityClass(ms);
        setResultType(ms, entityClass);
    }

    public void selectOptions(MappedStatement ms) {
        initResultType(ms);
    }

    public void selectOptionsByPrimaryKey(MappedStatement ms) {
        initResultType(ms);
    }

    /**
     * 按照主键查询SQL.
     *
     * @param dto record
     * @return sql
     */
    public String selectOptionsByPrimaryKey(BaseDTO dto) {
        return CustomHelper.buildSelectByPrimaryKeySQL(dto);
    }


    /**
     * 动态查询SQL.
     *
     * @param parameter parameter
     * @return sql
     */
    public String selectOptions(Map<String,Object> parameter) {
        BaseDTO dto = (BaseDTO)parameter.get(SelectOptionsMapper.OPTIONS_DTO);
        Criteria criteria = (Criteria)parameter.get(SelectOptionsMapper.OPTIONS_CRITERIA);
        return CustomHelper.buildSelectSelectiveSql(dto, criteria);
    }
}
