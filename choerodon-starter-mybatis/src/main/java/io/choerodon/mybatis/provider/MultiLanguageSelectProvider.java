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

import io.choerodon.mybatis.entity.CustomEntityTable;
import io.choerodon.mybatis.mapperhelper.CustomHelper;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.provider.base.BaseSelectProvider;

/**
 * BaseSelectProvider实现类，基础方法实现类
 *
 * @author liuzh
 */
public class MultiLanguageSelectProvider extends BaseSelectProvider {

    public MultiLanguageSelectProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 查询
     *
     * @param ms
     * @return
     */
    @Override
    public String selectOne(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        if (entityTable instanceof CustomEntityTable && ((CustomEntityTable) entityTable).isMultiLanguage()) {
            sql.append(CustomHelper.selectAllColumns_TL(entityClass));
            sql.append(CustomHelper.fromTable_TL(entityClass, tableName(entityClass)));
            sql.append(CustomHelper.whereAllIfColumns_TL(entityClass, isNotEmpty(), false));
            return sql.toString();
        } else {
            sql.append(SqlHelper.selectAllColumns(entityClass));
            sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
            sql.append(CustomHelper.whereAllIfColumns(entityClass, isNotEmpty(), false));
            return sql.toString();
        }
    }

    /**
     * 查询
     *
     * @param ms
     * @return
     */
    @Override
    public String select(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        if (entityTable instanceof CustomEntityTable && ((CustomEntityTable) entityTable).isMultiLanguage()) {
            sql.append(CustomHelper.selectAllColumns_TL(entityClass));
            sql.append(CustomHelper.fromTable_TL(entityClass, tableName(entityClass)));
            sql.append(CustomHelper.whereAllIfColumns_TL(entityClass, isNotEmpty(), false));
            sql.append(CustomHelper.orderByDefault_TL(entityClass));
            return sql.toString();
        } else {
            sql.append(SqlHelper.selectAllColumns(entityClass));
            sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
            sql.append(CustomHelper.whereAllIfColumns(entityClass, isNotEmpty(), false));
            sql.append(SqlHelper.orderByDefault(entityClass));
            return sql.toString();
        }
    }

    /**
     * 根据主键进行查询
     *
     * @param ms
     */
    @Override
    public String selectByPrimaryKey(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        if (entityTable instanceof CustomEntityTable && ((CustomEntityTable) entityTable).isMultiLanguage()) {
            //将返回值修改为实体类型
            setResultType(ms, entityClass);
            StringBuilder sql = new StringBuilder();
            sql.append(CustomHelper.selectAllColumns_TL(entityClass));
            sql.append(CustomHelper.fromTable_TL(entityClass, tableName(entityClass)));
            sql.append(CustomHelper.wherePKColumns(entityClass, null, false, true));
            return sql.toString();
        } else {
            return super.selectByPrimaryKey(ms);
        }
    }

    /**
     * 查询全部结果
     *
     * @param ms
     * @return
     */
    @Override
    public String selectAll(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        if (entityTable instanceof CustomEntityTable && ((CustomEntityTable) entityTable).isMultiLanguage()) {
            //修改返回值类型为实体类型
            setResultType(ms, entityClass);
            StringBuilder sql = new StringBuilder();
            sql.append(CustomHelper.selectAllColumns_TL(entityClass));
            sql.append(CustomHelper.fromTable_TL(entityClass, tableName(entityClass)));
            sql.append(CustomHelper.orderByDefault_TL(entityClass));
            return sql.toString();
        } else {
            return super.selectAll(ms);
        }
    }

    public String selectAllWithoutMultiLanguage(MappedStatement ms) {
        return super.selectAll(ms);
    }

    /**
     * 根据Example查询
     *
     * @param ms
     * @return
     */
    public String selectByExample(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        if (entityTable instanceof CustomEntityTable && ((CustomEntityTable) entityTable).isMultiLanguage()) {
            //将返回值修改为实体类型
            setResultType(ms, entityClass);
            StringBuilder sql = new StringBuilder("SELECT ");
            if (isCheckExampleEntityClass()) {
                sql.append(SqlHelper.exampleCheck(entityClass));
            }
            sql.append("<if test=\"distinct\">distinct</if>");
            //支持查询指定列
            sql.append(CustomHelper.exampleSelectColumns_TL(entityClass));
            sql.append(CustomHelper.fromTable_TL(entityClass, tableName(entityClass)));
            sql.append(SqlHelper.exampleWhereClause());
            sql.append(CustomHelper.exampleOrderBy_TL(entityClass));
            sql.append(SqlHelper.exampleForUpdate());
            return sql.toString();
        } else {
            //将返回值修改为实体类型
            setResultType(ms, entityClass);
            StringBuilder sql = new StringBuilder("SELECT ");
            if (isCheckExampleEntityClass()) {
                sql.append(SqlHelper.exampleCheck(entityClass));
            }
            sql.append("<if test=\"distinct\">distinct</if>");
            //支持查询指定列
            sql.append(SqlHelper.exampleSelectColumns(entityClass));
            sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
            sql.append(SqlHelper.exampleWhereClause());
            sql.append(SqlHelper.exampleOrderBy(entityClass));
            sql.append(SqlHelper.exampleForUpdate());
            return sql.toString();
        }
    }

}
