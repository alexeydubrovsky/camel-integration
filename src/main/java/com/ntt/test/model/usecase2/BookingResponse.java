package com.ntt.test.model.usecase2;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "booking")
public class BookingResponse {
    public String from;
    public String to;
    public String date;
    public String provider;
    public String amount;
}
