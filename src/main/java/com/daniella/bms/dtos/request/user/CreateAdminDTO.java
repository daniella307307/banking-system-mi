package com.daniella.bms.dtos.request.user;

import com.daniella.bms.dtos.request.auth.RegisterDTO;
import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Getter
@Setter
public class CreateAdminDTO extends RegisterDTO {
    private String adminCreateCode;
    public CreateAdminDTO(RegisterDTO registerDTO,String adminCreateCode){

    }
}
