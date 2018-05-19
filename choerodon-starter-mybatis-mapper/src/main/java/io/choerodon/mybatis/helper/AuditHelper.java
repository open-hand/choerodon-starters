package io.choerodon.mybatis.helper;

import java.util.Date;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.mybatis.domain.Audit;


/**
 * Created by xausky on 3/20/17.
 */
public class AuditHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditHelper.class);
    private static ThreadLocal<Audit> audits = new ThreadLocal<>();

    private AuditHelper() {
    }

    /**
     * 静态初始化
     *
     * @return Audit
     */
    public static Audit audit() {
        Audit audit = audits.get();
        if (audit == null) {
            audit = new Audit();
            audits.set(audit);
        }
        audit.setNow(new Date());
        CustomUserDetails details = DetailsHelper.getUserDetails();
        if (details != null) {
            audit.setUser(details.getUserId());
        } else {
            if (audit.getUser() == null) {
                audit.setUser(0L);
                LOGGER.warn("principal not instanceof CustomUserDetails audit user is 0L");
            }
        }
        return audit;
    }
}
