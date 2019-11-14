package io.choerodon.mybatis.autoconfigure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CustomPageableResolver implements PageableArgumentResolver {

    private static final SortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new SortHandlerMethodArgumentResolver();
    private static final String INVALID_DEFAULT_PAGE_SIZE = "Invalid default page size configured for method %s! Must not be less than one!";

    private static final String DEFAULT_PAGE_PARAMETER = "page";
    private static final String DEFAULT_SIZE_PARAMETER = "size";
    private static final String DEFAULT_PREFIX = "";
    private static final String DEFAULT_QUALIFIER_DELIMITER = "_";
    private static final int DEFAULT_MAX_PAGE_SIZE = 2000;
    static final Pageable DEFAULT_PAGE_REQUEST = PageRequest.of(0, 20);

    private Pageable fallbackPageable = DEFAULT_PAGE_REQUEST;
    private SortArgumentResolver sortResolver;
    private String pageParameterName = DEFAULT_PAGE_PARAMETER;
    private String sizeParameterName = DEFAULT_SIZE_PARAMETER;
    private String prefix = DEFAULT_PREFIX;
    private String qualifierDelimiter = DEFAULT_QUALIFIER_DELIMITER;
    private int maxPageSize = DEFAULT_MAX_PAGE_SIZE;
    private boolean oneIndexedParameters = false;


    public CustomPageableResolver() {
        this((SortArgumentResolver) null);
    }

    public CustomPageableResolver(SortHandlerMethodArgumentResolver sortResolver) {
        this((SortArgumentResolver) sortResolver);
    }

    public CustomPageableResolver(@Nullable SortArgumentResolver sortResolver) {
        this.sortResolver = sortResolver == null ? DEFAULT_SORT_RESOLVER : sortResolver;
    }

    public void setFallbackPageable(Pageable fallbackPageable) {

        Assert.notNull(fallbackPageable, "Fallback Pageable must not be null!");

        this.fallbackPageable = fallbackPageable;
    }

    public boolean isFallbackPageable(Pageable pageable) {
        return fallbackPageable == null ? false : fallbackPageable.equals(pageable);
    }

    public void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    protected int getMaxPageSize() {
        return this.maxPageSize;
    }

    public void setPageParameterName(String pageParameterName) {

        Assert.hasText(pageParameterName, "Page parameter name must not be null or empty!");
        this.pageParameterName = pageParameterName;
    }

    protected String getPageParameterName() {
        return this.pageParameterName;
    }

    public void setSizeParameterName(String sizeParameterName) {

        Assert.hasText(sizeParameterName, "Size parameter name must not be null or empty!");
        this.sizeParameterName = sizeParameterName;
    }

    protected String getSizeParameterName() {
        return this.sizeParameterName;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? DEFAULT_PREFIX : prefix;
    }

    public void setQualifierDelimiter(String qualifierDelimiter) {
        this.qualifierDelimiter = qualifierDelimiter == null ? DEFAULT_QUALIFIER_DELIMITER : qualifierDelimiter;
    }

    public void setOneIndexedParameters(boolean oneIndexedParameters) {
        this.oneIndexedParameters = oneIndexedParameters;
    }

    protected boolean isOneIndexedParameters() {
        return this.oneIndexedParameters;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.equals(parameter.getParameterType());
    }

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer mavContainer,
                                    NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {

        assertPageableUniqueness(methodParameter);

        Optional<Pageable> defaultOrFallback = getDefaultFromAnnotationOrFallback(methodParameter).toOptional();

        String pageString = webRequest.getParameter(getParameterNameToUse(pageParameterName, methodParameter));
        String pageSizeString = webRequest.getParameter(getParameterNameToUse(sizeParameterName, methodParameter));

        Optional<Integer> page = parseAndApplyBoundaries(pageString, Integer.MAX_VALUE, true);
        Optional<Integer> pageSize = parseAndApplyBoundaries(pageSizeString, maxPageSize, false);

        if (!(page.isPresent() && pageSize.isPresent()) && !defaultOrFallback.isPresent()) {
            return Pageable.unpaged();
        }

        int p = page
                .orElseGet(() -> defaultOrFallback.map(Pageable::getPageNumber).orElseThrow(IllegalStateException::new));
        int ps = pageSize
                .orElseGet(() -> defaultOrFallback.map(Pageable::getPageSize).orElseThrow(IllegalStateException::new));

        // Limit upper bound
        ps = ps > maxPageSize ? maxPageSize : ps;

        Sort sort = sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        if (ps == 0) {
            return CustomPageRequest.of(p, ps, sort.isSorted() ? sort : defaultOrFallback.map(Pageable::getSort).orElseGet(Sort::unsorted));
        }
        return PageRequest.of(p, ps,
                sort.isSorted() ? sort : defaultOrFallback.map(Pageable::getSort).orElseGet(Sort::unsorted));
    }

    protected String getParameterNameToUse(String source, @Nullable MethodParameter parameter) {

        StringBuilder builder = new StringBuilder(prefix);

        Qualifier qualifier = parameter == null ? null : parameter.getParameterAnnotation(Qualifier.class);

        if (qualifier != null) {
            builder.append(qualifier.value());
            builder.append(qualifierDelimiter);
        }

        return builder.append(source).toString();
    }

    private Pageable getDefaultFromAnnotationOrFallback(MethodParameter methodParameter) {

        PageableDefault defaults = methodParameter.getParameterAnnotation(PageableDefault.class);

        if (defaults != null) {
            return getDefaultPageRequestFrom(methodParameter, defaults);
        }

        return fallbackPageable;
    }

    private static Pageable getDefaultPageRequestFrom(MethodParameter parameter, PageableDefault defaults) {

        Integer defaultPageNumber = defaults.page();
        Integer defaultPageSize = getSpecificPropertyOrDefaultFromValue(defaults, "size");

        if (defaultPageSize < 1) {
            Method annotatedMethod = parameter.getMethod();
            throw new IllegalStateException(String.format(INVALID_DEFAULT_PAGE_SIZE, annotatedMethod));
        }

        if (defaults.sort().length == 0) {
            return PageRequest.of(defaultPageNumber, defaultPageSize);
        }

        return PageRequest.of(defaultPageNumber, defaultPageSize, defaults.direction(), defaults.sort());
    }

    private Optional<Integer> parseAndApplyBoundaries(@Nullable String parameter, int upper, boolean shiftIndex) {

        if (!StringUtils.hasText(parameter)) {
            return Optional.empty();
        }

        try {
            int parsed = Integer.parseInt(parameter) - (oneIndexedParameters && shiftIndex ? 1 : 0);
            return Optional.of(parsed < 0 ? 0 : parsed > upper ? upper : parsed);
        } catch (NumberFormatException e) {
            return Optional.of(0);
        }
    }

    public static void assertPageableUniqueness(MethodParameter parameter) {

        Method method = parameter.getMethod();

        if (method == null) {
            throw new IllegalArgumentException(String.format("Method parameter %s is not backed by a method.", parameter));
        }

        if (containsMoreThanOnePageableParameter(method)) {
            Annotation[][] annotations = method.getParameterAnnotations();
            assertQualifiersFor(method.getParameterTypes(), annotations);
        }
    }

    private static boolean containsMoreThanOnePageableParameter(Method method) {

        boolean pageableFound = false;

        for (Class<?> type : method.getParameterTypes()) {

            if (pageableFound && type.equals(Pageable.class)) {
                return true;
            }

            if (type.equals(Pageable.class)) {
                pageableFound = true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getSpecificPropertyOrDefaultFromValue(Annotation annotation, String property) {

        Object propertyDefaultValue = AnnotationUtils.getDefaultValue(annotation, property);
        Object propertyValue = AnnotationUtils.getValue(annotation, property);

        Object result = ObjectUtils.nullSafeEquals(propertyDefaultValue, propertyValue) //
                ? AnnotationUtils.getValue(annotation) //
                : propertyValue;

        if (result == null) {
            throw new IllegalStateException("Exepected to be able to look up an annotation property value but failed!");
        }

        return (T) result;
    }

    public static void assertQualifiersFor(Class<?>[] parameterTypes, Annotation[][] annotations) {

        Set<String> values = new HashSet<>();

        for (int i = 0; i < annotations.length; i++) {

            if (Pageable.class.equals(parameterTypes[i])) {

                Qualifier qualifier = findAnnotation(annotations[i]);

                if (null == qualifier) {
                    throw new IllegalStateException(
                            "Ambiguous Pageable arguments in handler method. If you use multiple parameters of type Pageable you need to qualify them with @Qualifier");
                }

                if (values.contains(qualifier.value())) {
                    throw new IllegalStateException("Values of the user Qualifiers must be unique!");
                }

                values.add(qualifier.value());
            }
        }
    }

    @Nullable
    private static Qualifier findAnnotation(Annotation[] annotations) {

        for (Annotation annotation : annotations) {
            if (annotation instanceof Qualifier) {
                return (Qualifier) annotation;
            }
        }

        return null;
    }
}
