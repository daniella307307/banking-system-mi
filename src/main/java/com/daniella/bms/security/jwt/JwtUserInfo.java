package com.daniella.bms.security.jwt;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class JwtUserInfo {
    private UUID userId;
    private String email;
    private List<String> role;
}
