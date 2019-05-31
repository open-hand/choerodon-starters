package io.choerodon.core.convertor;

import static io.choerodon.core.convertor.ConvertHelper.getDestinClassData;
import static io.choerodon.core.convertor.ConvertHelper.invokeConvert;

import java.util.ArrayList;
import java.util.List;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.domain.Page;

/**
 * page的转换
 * 因为需要依赖于mybatis，所以独立写了一个类
 * 移除该类的时候，可以移除pom里的PageHelper依赖
 *
 * @author flyleft
 * 2018/3/19
 */
@Deprecated
public class ConvertPageHelper {

    private ConvertPageHelper() {
    }

    /**
     * page的转换
     *
     * @param pageSource 要转换的page对象
     * @param destin     要转换的目标类型的Class
     * @param <T>        要转换的目标类型
     * @return 转换的后page对象
     */
    public static <T> Page<T> convertPage(final Page pageSource, final Class<T> destin) {
        Page<T> pageBack = new Page<>();
        pageBack.setNumber(pageSource.getNumber());
        pageBack.setNumberOfElements(pageSource.getNumberOfElements());
        pageBack.setSize(pageSource.getSize());
        pageBack.setTotalElements(pageSource.getTotalElements());
        pageBack.setTotalPages(pageSource.getTotalPages());
        if (pageSource.getContent().isEmpty()) {
            return pageBack;
        }

        Class<?> source = pageSource.getContent().get(0).getClass();
        if (source.getTypeName().contains(ConvertHelper.SPRING_PROXY_CLASS)) {
            source = source.getSuperclass();
        }
        final ConvertHelper.DestinClassData destinClassData = getDestinClassData(source, destin);
        List<T> list = new ArrayList<>(pageSource.getContent().size());
        for (Object object : pageSource.getContent()) {
            T t = invokeConvert(destinClassData, object);
            list.add(t);
        }
        pageBack.setContent(list);
        return pageBack;
    }

    /**
     * PageInfo转换器
     *
     * @param pageInfo
     * @param destin
     * @param <T>
     * @return
     */
    public static <T> PageInfo<T> convertPageInfo(final PageInfo pageInfo, final Class<T> destin) {
        com.github.pagehelper.Page<T> page = new com.github.pagehelper.Page<>(pageInfo.getPages(), pageInfo.getSize());
        page.setTotal(pageInfo.getTotal());
        if (pageInfo.getList().isEmpty()) {
            return page.toPageInfo();
        }

        Class<?> source = pageInfo.getList().get(0).getClass();
        if (source.getTypeName().contains(ConvertHelper.SPRING_PROXY_CLASS)) {
            source = source.getSuperclass();
        }
        final ConvertHelper.DestinClassData destinClassData = getDestinClassData(source, destin);
        List<T> list = new ArrayList<>(pageInfo.getList().size());
        for (Object object : pageInfo.getList()) {
            T t = invokeConvert(destinClassData, object);
            list.add(t);
        }
        page.addAll(list);
        return page.toPageInfo();
    }

}
