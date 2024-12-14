package org.atlas.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthInfo {

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;
}
