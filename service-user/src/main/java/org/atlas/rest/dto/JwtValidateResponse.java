package org.atlas.rest.dto;

import java.util.List;
import lombok.Data;

@Data
public class JwtValidateResponse {

    private List<String> authorities;

    private String password;

    private String subject;

    private Long userId;

    private boolean isValid;

}
