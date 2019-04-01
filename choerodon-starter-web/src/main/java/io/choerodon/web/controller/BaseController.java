package io.choerodon.web.controller;

import com.google.common.base.Throwables;
import io.choerodon.base.exception.IBaseException;
import io.choerodon.web.DefaultConfiguration;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import io.choerodon.web.dto.ResponseData;
import io.choerodon.web.util.RequestUtil;
import io.choerodon.web.validator.FieldErrorWithBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * BaseController.
 *
 * @author njq.niu@hand-china.com
 * @since 2016年1月5日
 */
@RestController
public class BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
    protected static final String DEFAULT_PAGE = "1";
    protected static final String DEFAULT_PAGE_SIZE = "10";

    protected static final String SYS_VALIDATION_PREFIX = "hap.validation.";

    protected static final String DEFAULT_VIEW_HOME = "";

    @Autowired
    private DefaultConfiguration configuration;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Validator validator;

    protected String getViewPath() {
        if (configuration != null) {
            return configuration.getDefaultViewPath();
        }
        return DEFAULT_VIEW_HOME;
    }

    protected Validator getValidator() {
        return validator;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    protected IRequest createRequestContext(HttpServletRequest request) {
        return RequestHelper.createServiceRequest(request);
    }

    /**
     * 处理控制层所有异常.
     *
     * @param exception 未捕获的异常
     * @param request   HttpServletRequest
     * @return ResponseData(BaseException 被处理) 或者 ModelAndView(其他 Exception
     * ,500错误)
     */
    @ExceptionHandler(value = {Exception.class})
    public Object exceptionHandler(Exception exception, HttpServletRequest request) {
        logger.error(exception.getMessage(), exception);
        Throwable thr = Throwables.getRootCause(exception);
        if (RequestUtil.isAjaxRequest(request) || RequestUtil.isAPIRequest(request) || RequestUtil.isMultipartRequest(request)) {
            ResponseData res = new ResponseData(false);
            if (thr instanceof IBaseException) {
                IBaseException be = (IBaseException) thr;
                Locale locale = RequestContextUtils.getLocale(request);
                String messageKey = be.getDescriptionKey();
                String message = messageSource.getMessage(messageKey, be.getParameters(), messageKey, locale);
                res.setCode(be.getCode());
                res.setMessage(message);
            } else {
                res.setMessage(thr.toString());
            }
            return res;
        } else {
            ModelAndView view = new ModelAndView("500");
            if (thr instanceof IBaseException) {
                IBaseException be = (IBaseException) thr;
                Locale locale = RequestContextUtils.getLocale(request);
                String messageKey = be.getDescriptionKey();
                String message = messageSource.getMessage(messageKey, be.getParameters(), messageKey, locale);
                view.addObject("message", message);
            }
            return view;
        }
    }

    /**
     * 标准校验,错误消息获取.
     *
     * @param errors  包含错误的对象
     * @param request HttpServletRequest
     * @return 经过翻译的错误消息
     */
    protected String getErrorMessage(Errors errors, HttpServletRequest request) {
        Locale locale = RequestContextUtils.getLocale(request);
        String errorMessage = null;
        for (ObjectError error : errors.getAllErrors()) {
            if (StringUtils.isEmpty(error.getDefaultMessage())) {
                errorMessage = error.getCode();
            } else {
                if (error instanceof FieldErrorWithBean) {
                    errorMessage = getStandardFieldErrorMessage((FieldErrorWithBean) error, locale);
                } else {
                    errorMessage = messageSource.getMessage(error.getDefaultMessage(), null, locale);
                }
                break;
            }
        }
        return errorMessage;
    }

    /**
     * translate code to message.
     *
     * @param request http servlet request
     * @param code    code
     * @param args    params used in message
     * @return translated message or original code
     */
    protected String nls(HttpServletRequest request, String code, Object[] args) {
        Locale locale = RequestContextUtils.getLocale(request);
        return messageSource.getMessage(code, args, code, locale);
    }

    /**
     * translate code to message.
     *
     * @param request http servlet request
     * @param code    code
     * @return translated message or original code
     */
    protected String nls(HttpServletRequest request, String code) {
        Locale locale = RequestContextUtils.getLocale(request);
        return messageSource.getMessage(code, null, code, locale);
    }

    /**
     * 取得字段校验的标准错误消息.
     * <p>
     * 诸如 NotEmpty 之类的标准错误,可以同过次方法取得错误消息
     *
     * @param fieldError 可以取到 targetBean
     * @param locale     当前语言环境
     * @return 与当前语言环境相符的错误描述
     */
    protected String getStandardFieldErrorMessage(FieldErrorWithBean fieldError, Locale locale) {
        String field = fieldError.getField();
        Class clazz = fieldError.getTargetBean().getClass();
        clazz = findDeclareClass(clazz, field);
        String fieldPromptMessageKey = clazz.getSimpleName() + "." + field;
        String fieldPrompt = messageSource.getMessage(fieldPromptMessageKey.toLowerCase(), null, locale);

        String code = SYS_VALIDATION_PREFIX + fieldError.getCode().toLowerCase();
        String msg = messageSource.getMessage(code, new Object[]{fieldPrompt}, fieldError.getDefaultMessage(), locale);
        if (code.equalsIgnoreCase(msg) && fieldError.getDefaultMessage() != null) {
            msg = fieldPrompt + " : " + fieldError.getDefaultMessage();
        }
        return msg;
    }

    /**
     * 找到是哪一个父类定义了属性.
     *
     * @param fromClass 从哪一个类开始查找
     * @param fieldName 属性名
     * @return 定义了指定属性的类, 找不到的话, 返回fromClass
     */
    private Class findDeclareClass(Class fromClass, String fieldName) {
        Class clazz = fromClass;
        while (clazz.getSuperclass() != null) {
            try {
                clazz.getDeclaredField(fieldName);
                return clazz;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return fromClass;
    }

}
