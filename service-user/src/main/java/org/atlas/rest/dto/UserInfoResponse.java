package org.atlas.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {

    private String userName;

    private String userPhoneNumber;

    private Integer age;

    private String gender;

    private String userAvatar;

}
