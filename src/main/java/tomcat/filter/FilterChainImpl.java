package tomcat.filter;

import org.reflections.Reflections;
import tomcat.HttpServletRequest;
import tomcat.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author yousj
 * @since 2020/12/12
 */
public class FilterChainImpl implements FilterChain {

    // 过滤器容器
    public static List<Filter> filters = null;
    // 当前执行到的过滤器位置
    private int currentPosition = 0;

    public FilterChainImpl() {
        // 初始化过滤器容器
        initFilters();
    }

    private void initFilters() {
        try {
            if (filters == null) {
                Set<Class<? extends Filter>> classes = new Reflections("tomcat").getSubTypesOf(Filter.class);
                if (classes.size() > 0) {
                    filters = new ArrayList<>();
                    // 添加过滤器
                    for (Class<?> clazz : classes) {
                        filters.add((Filter) clazz.newInstance());
                    }
                    // 排序
                    filters.sort((x, y) -> Integer.compare(y.order(), x.order()));
                    // 执行init方法
                    for (Filter filter : filters) {
                        filter.init(null);
                    }
                }
            }
            System.out.println("================== filter init success ================");
        } catch (Exception e) {
            System.err.print(" init filter error.");
            e.printStackTrace();
        }
    }

    /**
     * 执行过滤器链
     */
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (filters.size() > 0 && currentPosition != filters.size()) {
            ++currentPosition;
            filters.get(currentPosition - 1).doFilter(request, response, this);
        }
    }
}
