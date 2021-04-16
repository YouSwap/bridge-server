package you.tools.rabbitmq.producer;


import you.tools.rabbitmq.RabbitTableInitRequest;

public interface MsgProducer {
    /**
     *
     * @param exchangeKey
     * @param routingKey
     * @param initRequest RabbitTableInitRequest对象参数
     */
    void sendMsg(String exchangeKey, String routingKey, RabbitTableInitRequest initRequest);


}
