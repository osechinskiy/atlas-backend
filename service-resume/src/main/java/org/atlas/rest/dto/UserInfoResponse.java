package org.atlas.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {

    private String userName;

    private String userPhoneNumber;

    private Integer age;

    private String gender;

    private String userAvatar;

}
