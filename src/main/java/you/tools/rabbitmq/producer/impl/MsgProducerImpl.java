package you.tools.rabbitmq.producer.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import you.tools.rabbitmq.RabbitTableInitRequest;
import you.tools.rabbitmq.producer.MsgProducer;

import java.util.UUID;

@Slf4j
@Service("msgProducer")
public class MsgProducerImpl implements MsgProducer {

    //由于rabbitTemplate的scope属性设置为ConfigurableBeanFactory.SCOPE_PROTOTYPE，所以不能自动注入
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //回调函数: confirm确认
    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            String messageId = correlationData.getId();
            if(ack){
                log.info("消息成功消费"+messageId);
            } else {
                //失败则进行具体的后续操作:重试 或者补偿等手段
                log.info("消息消费失败:" + cause);
            }
        }
    };
    /**
     *
     * @param routingKey
     * @param initRequest RabbitTableInitRequest对象参数
     */
    @Override
    public void sendMsg(String exchangeKey,String routingKey, RabbitTableInitRequest initRequest) {
        // 通过实现 ConfirmCallback 接口，消息发送到 Broker 后触发回调，确认消息是否到达 Broker 服务器，也就是只确认是否正确到达 Exchange 中
        rabbitTemplate.setConfirmCallback(confirmCallback);
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        //把消息放入ROUTINGKEY_A对应的队列当中去，对应的是队列exchangeKey
        rabbitTemplate.convertAndSend(exchangeKey, routingKey, initRequest, correlationId);
    }


}
