package com.emotorad.Emotorad.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class jsonParser {

    private String email;

    private String phone;

    private String product;

}
