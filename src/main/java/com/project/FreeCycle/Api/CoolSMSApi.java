package com.project.FreeCycle.Api;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component(value = "coolSMSApi")
public class CoolSMSApi {

    @Value("${coolsms.apikey}")
    private String apikey;

    @Value("${coolsms.apisecret}")
    private String apiSecret;

    @Value("${coolsms.fromnumber}")
    private String fromNumber;
}
