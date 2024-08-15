package ua.corporation.memeclimb.impl;

import ua.corporation.memeclimb.entity.main.PaymentInformation;

public interface PaymentOperation {
    String sendPaymentTransaction(PaymentInformation paymentInformation);

}
