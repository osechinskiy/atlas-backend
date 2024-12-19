package org.atlas.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserinfoDto {

    private Long userId;

    private String userName;

    private String avatar;
}
