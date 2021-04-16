package you.tools.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;
import you.tools.utils.SpringContextUtil;

import java.lang.reflect.Method;

@Slf4j
public class RabbitmqRunnable implements Runnable {
    private Object target;
    private Method method;
    private RabbitTableInitRequest params;

    RabbitmqRunnable(String beanName, String methodName, RabbitTableInitRequest params) throws NoSuchMethodException, SecurityException {
        this.target = SpringContextUtil.getBean(beanName);
        this.params = params;

        if (null!=params) {
            this.method = target.getClass().getDeclaredMethod(methodName, RabbitTableInitRequest.class);
        } else {
            this.method = target.getClass().getDeclaredMethod(methodName);
        }
    }

    @Override
    public void run() {
        try {
            ReflectionUtils.makeAccessible(method);
            if (null!=params) {
                method.invoke(target, params);
            } else {
                method.invoke(target);
            }
        } catch (Exception e) {
            log.error("队列任务失败", e);
        }
    }
}
