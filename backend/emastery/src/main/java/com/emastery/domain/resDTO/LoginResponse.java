package com.emastery.domain.resDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    public boolean Success;
    public String Message;
    public UserInfo UserInfo;
}
