package org.atlas.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeDto {

    private Long id;

    private long userId;

    private String userName;

    private String userAvatar;

    private String userPhone;

    private Integer age;

    private String gender;

    private String category;

    private String title;

    private String description;

    private String creationTimestamp;

    private Integer experience;

    private boolean editable;
}
