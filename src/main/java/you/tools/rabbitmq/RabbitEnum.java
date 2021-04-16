package you.tools.rabbitmq;

public class RabbitEnum {
    public RabbitEnum() {
    }

    public static enum QueueRouting {
        routingkey_one("第一个路由","routingkey_one"),
        routingkey_two("第二个路由","routingkey_two"),
        routingkey_three("第三个路由","routingkey_three"),
        routingkey_four("第四个路由","routingkey_four");


        private String name;
        private String index;

        private QueueRouting(String name, String index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIndex() {
            return this.index;
        }

        public void setIndex(String index) {
            this.index = index;
        }
    }
}
