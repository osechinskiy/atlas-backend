package org.atlas.service;

import org.atlas.model.OrderInfo;

public interface DataSender {
    void send(OrderInfo orderInfo);
}
