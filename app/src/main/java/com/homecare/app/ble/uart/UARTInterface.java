package com.homecare.app.ble.uart;

import com.homecare.app.model.ble.ExchangeInfo;

public interface UARTInterface {
    public void send(final byte[] bytes);

    public void sendAckedRequest(ExchangeInfo request, int maxTryTimes);
}
