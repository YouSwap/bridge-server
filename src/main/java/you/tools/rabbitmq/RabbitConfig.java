package you.tools.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 Broker:它提供一种传输服务,它的角色就是维护一条从生产者到消费者的路线，保证数据能按照指定的方式进行传输,
 Exchange：消息交换机,它指定消息按什么规则,路由到哪个队列。
 Queue:消息的载体,每个消息都会被投到一个或多个队列。
 Binding:绑定，它的作用就是把exchange和queue按照路由规则绑定起来.
 Routing Key:路由关键字,exchange根据这个关键字进行消息投递。
 vhost:虚拟主机,一个broker里可以有多个vhost，用作不同用户的权限分离。
 Producer:消息生产者,就是投递消息的程序.
 Consumer:消息消费者,就是接受消息的程序.
 Channel:消息通道,在客户端的每个连接里,可建立多个channel.
 */
@Configuration
public class RabbitConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtualHost}")
    private String virtualHost;


    public static final String EXCHANGE_A = "topic_exchage";

    public static final String DIRECT_A = "direct_exchage";



    public static final String queue_one = "queue_one";//oneRouting
    public static final String queue_two = "queue_two";//twoRouting
    public static final String queue_three = "queue_three";//threeRouting
    public static final String queue_four = "queue_four";//threeRouting

    public static final String routingkey_one = RabbitEnum.QueueRouting.routingkey_one.getIndex();
    public static final String routingkey_two = RabbitEnum.QueueRouting.routingkey_two.getIndex();
    public static final String routingkey_three = RabbitEnum.QueueRouting.routingkey_three.getIndex();
    public static final String routingkey_four = RabbitEnum.QueueRouting.routingkey_four.getIndex();


    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }
    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     HeadersExchange ：通过添加属性key-value匹配
     DirectExchange:按照routingkey分发到指定队列
     TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_A);
    }
    @Bean
    public TopicExchange defaultExchange() {
        return new TopicExchange(EXCHANGE_A);
    }
    /**
     * 获取队列路由任务
     * @return
     */
    @Bean
    public Queue queue_one() {
        return new Queue(queue_one, true); //队列持久
    }
    /**
     * 将队列路由绑定到交换机
     * @return
     */
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue_one()).to(defaultExchange()).with(routingkey_one);
    }

    /**
     * 获取队列路由任务
     * @return
     */
    @Bean
    public Queue queuetwo() {
        return new Queue(queue_two, true); //队列持久
    }
    /**
     * 将队列路由绑定到交换机
     * @return
     */
    @Bean
    public Binding bindingB() {
        return BindingBuilder.bind(queuetwo()).to(defaultExchange()).with(routingkey_two);
    }

    /**
     * 获取队列路由任务
     * @return
     */
    @Bean
    public Queue queuethree() {
        return new Queue(queue_three, true); //队列持久
    }
    /**
     * 将队列路由绑定到交换机
     * @return
     */
    @Bean
    public Binding bindingC() {
        return BindingBuilder.bind(queuethree()).to(defaultExchange()).with(routingkey_three);
    }

    /**
     * 获取队列路由任务
     * @return
     */
    @Bean
    public Queue queue_four() {
        return new Queue(queue_four, true); //队列持久
    }
    /**
     * 将队列路由绑定到交换机
     * @return
     */
    @Bean
    public Binding bindingD() {
        return BindingBuilder.bind(queue_four()).to(defaultExchange()).with(routingkey_four);
    }



}