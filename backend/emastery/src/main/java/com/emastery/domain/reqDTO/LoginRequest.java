package com.emastery.domain.reqDTO;

import com.emastery.domain.Enum.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    private String fullName;
    private String username;
    private String password;
    private String gender;
    private Integer age;
    private Level englishLevel;
}