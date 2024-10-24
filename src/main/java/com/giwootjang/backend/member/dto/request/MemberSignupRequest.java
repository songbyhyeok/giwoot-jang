package com.giwootjang.backend.member.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class MemberSignupRequest {
    private String name;
    private String id;
    private String password;
    private String passwordChk;
    private String email;
    private String fPhoneN;
    private String mPhoneN;
    private String bPhoneN;
}
