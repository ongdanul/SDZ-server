package com.elice.sdz.user.dto.response;

import com.elice.sdz.user.dto.UserAccountDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccountResponseDTO {

    private List<UserAccountDTO> emails;
    private String message;
}
