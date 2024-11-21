package com.giwootjang.backend.sms.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SmsAuthRequest {
    @JsonProperty("name")
    private String name;
    @JsonProperty("fPhoneN")
    private String fPhoneN;
    @JsonProperty("mPhoneN")
    private String mPhoneN;
    @JsonProperty("bPhoneN")
    private String bPhoneN;
}
