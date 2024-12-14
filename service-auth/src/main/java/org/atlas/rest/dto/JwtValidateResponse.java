package org.atlas.rest.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtValidateResponse {

    private List<String> authorities;

    private String password;

    private String subject;

    private Long userId;

    private boolean isValid;
}
