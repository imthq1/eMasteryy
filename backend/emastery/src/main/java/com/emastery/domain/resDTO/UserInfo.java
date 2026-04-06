package com.emastery.domain.resDTO;

import com.emastery.domain.Enum.Level;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    public String FullName;

    public String Gender;

    public int Age;

    @Enumerated(EnumType.STRING)
    public Level englishLevel;
}
