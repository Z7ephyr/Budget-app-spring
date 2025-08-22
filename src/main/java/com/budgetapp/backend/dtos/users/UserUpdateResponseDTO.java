package com.budgetapp.backend.dtos.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateResponseDTO {

    private UserDTO user;


    private String token;
}
