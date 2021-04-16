package you.tools.rabbitmq;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import you.tools.utils.SpringContextUtil;

import java.lang.reflect.Method;

@Slf4j
@Component
@RabbitListener(queues = {RabbitConfig.queue_one,RabbitConfig.queue_two,RabbitConfig.queue_three})
public class MsgReceiver {

    @RabbitHandler//如果有消息过来，在消费的时候调用这个方法
    public void process(RabbitTableInitRequest initRequest) {
        log.info("onMessage接收到一个rabbitmq执行命令：调用实现类1："+ initRequest.getInterfaceName()+",方法名："+initRequest.getMethodName());
        try {
            Object target =  SpringContextUtil.getBean(initRequest.getInterfaceName());
            Method method;
            if (null!=initRequest) {
                method = target.getClass().getDeclaredMethod(initRequest.getMethodName(), RabbitTableInitRequest.class);
            } else {
                method = target.getClass().getDeclaredMethod(initRequest.getMethodName());
            }
            ReflectionUtils.makeAccessible(method);
            if (null!=initRequest) {
                method.invoke(target, initRequest);
            } else {
                method.invoke(target);
            }
        } catch (Exception e) {
            log.error("队列任务失败", e);
        }finally {

        }
    }

}
