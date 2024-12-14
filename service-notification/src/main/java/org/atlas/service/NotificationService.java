package org.atlas.service;

import org.atlas.model.OrderInfo;

public interface NotificationService {

    void consume(OrderInfo orderInfo);
}
