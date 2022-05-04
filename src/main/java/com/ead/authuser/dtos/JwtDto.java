package com.ead.authuser.dtos;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class JwtDto {

    @NonNull
    private String token;
    @NotBlank
    private String type = "Bearer";
}
