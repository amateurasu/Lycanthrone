package vn.elite.fundamental.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import lombok.val;

import static java.nio.charset.StandardCharsets.UTF_8;

public class EmitLog {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
        val factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (
            val connection = factory.newConnection();
            val channel = connection.createChannel()
        ) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            val message = argv.length < 1 ? "info: Hello World!" : String.join(" ", argv);

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(UTF_8));
            System.out.printf(" [x] Sent '%s'%n", message);
        }
    }
}

