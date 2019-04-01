package io.choerodon.mybatis.service;

import com.github.pagehelper.PageHelper;
import io.choerodon.mybatis.UpdateFailedException;
import io.choerodon.mybatis.common.Mapper;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.entity.Criteria;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Service
public abstract class BaseServiceImpl<T> implements IBaseService<T> {

    @Autowired
    protected Mapper<T> mapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> select(T condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return mapper.select(condition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public T insert(T record) {
        mapper.insert(record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public T insertSelective(T record) {
        mapper.insertSelective(record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public T updateByPrimaryKey(T record) {
        int ret = mapper.updateByPrimaryKey(record);
        checkOvn(ret, record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public T updateByPrimaryKeySelective(T record) {
        int ret = mapper.updateByPrimaryKeySelective(record);
        checkOvn(ret, record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public T updateByPrimaryKeyOptions(T record, Criteria criteria) {
        int ret = mapper.updateByPrimaryKeyOptions(record, criteria);
        checkOvn(ret, record);
        return record;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public T selectByPrimaryKey(T record) {
        return mapper.selectByPrimaryKey(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public int deleteByPrimaryKey(T record) {
        int ret = mapper.deleteByPrimaryKey(record);
        checkOvn(ret, record);
        return ret;
    }

    /**
     * 检查乐观锁<br>
     * 检测到更新，删除失败时，抛出UpdateFailedException 异常
     *
     * @param updateCount update,delete 操作返回的值
     * @param record      操作参数
     */
    @SuppressWarnings("unchecked")
    protected void checkOvn(int updateCount, Object record) {
        if (updateCount == 0 && record instanceof BaseDTO) {
            BaseDTO baseDTO = (BaseDTO) record;
            if (baseDTO.getObjectVersionNumber() != null) {
                throw new RuntimeException(new UpdateFailedException(baseDTO));
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> selectAll() {
        return mapper.selectAll();
    }

    /**
     * this method assume the object in list is BaseDTO.
     *
     * @param list dto list
     * @return the list
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public List<T> batchUpdate(List<T> list) {
        IBaseService<T> self = ((IBaseService<T>) AopContext.currentProxy());
        for (T t : list) {
            switch (((BaseDTO) t).get__status()) {
                case BaseDTO.STATUS_ADD:
                    self.insertSelective(t);
                    break;
                case BaseDTO.STATUS_UPDATE:
                    if (useSelectiveUpdate()) {
                        self.updateByPrimaryKeySelective(t);
                    } else {
                        self.updateByPrimaryKey(t);
                    }
                    break;
                case BaseDTO.STATUS_DELETE:
                    self.deleteByPrimaryKey(t);
                    break;
                default:
                    break;
            }
        }
        return list;
    }

    /**
     * 默认 true,表示在 batchUpdate 中,更新操作,使用updateByPrimaryKeySelective(只更新不为 null
     * 的字段)。<br>
     * 若返回 false,则使用 updateByPrimaryKey(更新所有字段)
     *
     * @return true:只更新不为 null的字段 false:更新所有字段
     */
    protected boolean useSelectiveUpdate() {
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public int batchDelete(List<T> list) {
        IBaseService<T> self = ((IBaseService<T>) AopContext.currentProxy());
        int c = 0;
        for (T t : list) {
            c += self.deleteByPrimaryKey(t);
        }
        return c;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    @SuppressWarnings("unchecked")
    public List<T> selectOptions(T record, Criteria criteria) {
        if (criteria == null) {
            criteria = new Criteria(record);
        }
        IBaseService<T> self = ((IBaseService<T>) AopContext.currentProxy());
        return self.selectOptions(record, criteria, null, null);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> selectOptions(T record, Criteria criteria, Integer pageNum, Integer pageSize) {
        if (pageNum != null && pageSize != null) {
            PageHelper.startPage(pageNum, pageSize);
        }
        if (criteria == null) {
            criteria = new Criteria(record);
        }
        return mapper.selectOptions(record, criteria);
    }

}
