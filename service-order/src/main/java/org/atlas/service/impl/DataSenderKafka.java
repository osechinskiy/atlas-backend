package org.atlas.service.impl;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.atlas.model.OrderInfo;
import org.atlas.service.DataSender;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j(topic = "KAFKA")
public class DataSenderKafka implements DataSender {

    private final KafkaTemplate<String, OrderInfo> template;

    private final Consumer<OrderInfo> sendAsk;

    private final String topic;


    public DataSenderKafka(KafkaTemplate<String, OrderInfo> template, Consumer<OrderInfo> sendAsk, String topic) {
        this.template = template;
        this.sendAsk = sendAsk;
        this.topic = topic;
    }

    @Override
    public void send(OrderInfo orderInfo) {
        try {
            log.info("value:{}", orderInfo);
            template.send(topic, orderInfo)
                    .whenComplete(
                            (result, ex) -> {
                                if (ex == null) {
                                    log.info(
                                            "message id:{} was sent, offset:{}",
                                            orderInfo.getOrderId(),
                                            result.getRecordMetadata().offset());
                                    sendAsk.accept(orderInfo);
                                } else {
                                    log.error("message id:{} was not sent", orderInfo.getOrderId(), ex);
                                }
                            });
        } catch (Exception ex) {
            log.error("send error, value:{}", orderInfo, ex);
        }
    }
}
