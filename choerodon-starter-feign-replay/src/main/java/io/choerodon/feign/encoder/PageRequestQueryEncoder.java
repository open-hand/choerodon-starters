package io.choerodon.feign.encoder;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * feign编码器，处理feign调用传PageRequest对象的情况
 * @author superlee
 * @since 2019-07-02
 */
public class PageRequestQueryEncoder implements Encoder {

    private final Encoder delegate;

    public PageRequestQueryEncoder(Encoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public void encode(Object object, Type type, RequestTemplate requestTemplate) throws EncodeException {
        if (object instanceof PageRequest) {
            PageRequest pageRequest = (PageRequest) object;
            requestTemplate.query("page", pageRequest.getPageNumber() + "");
            requestTemplate.query("size", pageRequest.getPageSize() + "");
            Collection<String> existingSorts = requestTemplate.queries().get("sort");
            List<String> sortQueries = existingSorts != null ? new ArrayList<>(existingSorts) : new ArrayList<>();
            for (Sort.Order order : pageRequest.getSort()) {
                sortQueries.add(order.getProperty() + "," + order.getDirection());
            }
            requestTemplate.query("sort", sortQueries);
        } else {
            delegate.encode(object, type, requestTemplate);
        }
    }
}
