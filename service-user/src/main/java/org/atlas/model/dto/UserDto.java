package org.atlas.model.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.atlas.model.UserPhoneNumber;
import org.atlas.model.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private long id;

    private String firstName;

    private String lastName;

    private List<UserPhoneNumber> phoneNumbers;

    private String email;

    private LocalDate birthday;

    private String gender;

    private Boolean isActive;

    private Role authority;

    private String avatar;
}
