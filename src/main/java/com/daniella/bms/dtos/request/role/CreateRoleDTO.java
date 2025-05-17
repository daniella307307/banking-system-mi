package com.daniella.bms.dtos.request.role;

import com.daniella.bms.enums.ERole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRoleDTO {
    @Schema(example = "ADMIN", description = "Role name")
    @NotNull
    private ERole name;
}
