package io.choerodon.core.asserts;

import io.choerodon.core.excel.ExceptionConstants;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * @author superlee
 * @since 2019-04-15
 */
public class DetailsHelperAssert {

    public static CustomUserDetails userDetailNotExisted() {
        return userDetailNotExisted(ExceptionConstants.CommonErrorCode.USER_NOT_LOGIN);
    }

    public static CustomUserDetails userDetailNotExisted(String message) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (userDetails == null) {
            throw new CommonException(message);
        }
        return userDetails;
    }


}
