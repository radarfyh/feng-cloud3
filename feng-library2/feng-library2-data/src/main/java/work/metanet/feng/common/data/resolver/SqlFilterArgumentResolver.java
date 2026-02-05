package work.metanet.feng.common.data.resolver;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.common.core.exception.CustomRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 解决Mybatis Plus Order By SQL注入问题
 * <p>
 * 该类实现了MyBatis Plus的分页请求参数解析器，支持动态生成分页查询SQL，并加入SQL注入防护。
 * </p>
 */
@Slf4j
public class SqlFilterArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String[] KEYWORDS = {"master", "truncate", "insert", "select", "delete", "update", "declare",
            "alter", "drop", "sleep", "extractvalue", "concat"};

    /**
     * 判断Controller是否包含page 参数
     * @param parameter 参数
     * @return 是否过滤
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Page.class);
    }

    /**
     * 解析请求参数，生成Page对象，并处理排序和分页相关的参数
     * @param parameter 入参集合
     * @param mavContainer model 和 view
     * @param webRequest web相关
     * @param binderFactory 入参解析
     * @return 检查后新的page对象
     * <p>
     * page 只支持查询 GET .如需解析POST获取请求报文体处理
     * </p>
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        // 获取请求参数
        String[] ascs = request.getParameterValues("ascs");
        String[] descs = request.getParameterValues("descs");
        String current = request.getParameter("current");
        String size = request.getParameter("size");

        // 生成Page对象
        Page page = new Page();
        if (StrUtil.isNotBlank(current)) {
            // 对current page进行合法性校验
            try {
                long currentPage = Long.parseLong(current);
                if (currentPage < 0) {
                    throw new CustomRuntimeException("current page error");
                }
                page.setCurrent(currentPage);
            } catch (NumberFormatException e) {
                log.error("Invalid current page number: {}", current, e);
                throw new CustomRuntimeException("Invalid current page number");
            }
        }

        if (StrUtil.isNotBlank(size)) {
            page.setSize(Long.parseLong(size));
        }

        // 处理排序字段
        List<OrderItem> orderItemList = new ArrayList<>();
        Optional.ofNullable(ascs).ifPresent(s -> orderItemList.addAll(
                Arrays.stream(s).filter(sqlInjectPredicate()).map(OrderItem::asc).collect(Collectors.toList())));
        Optional.ofNullable(descs).ifPresent(s -> orderItemList.addAll(
                Arrays.stream(s).filter(sqlInjectPredicate()).map(OrderItem::desc).collect(Collectors.toList())));
        page.addOrder(orderItemList);

        return page;
    }

    /**
     * 判断用户输入里面有没有关键字
     * @return Predicate
     */
    private Predicate<String> sqlInjectPredicate() {
        return sql -> {
            for (String keyword : KEYWORDS) {
                if (StrUtil.containsIgnoreCase(sql, keyword)) {
                    log.warn("Detected potential SQL injection keyword: {}", keyword);
                    return false;
                }
            }
            return true;
        };
    }
}
