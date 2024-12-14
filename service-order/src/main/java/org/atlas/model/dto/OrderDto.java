package org.atlas.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private long id;

    private long userId;

    private String userName;

    private String category;

    private String title;

    private String description;

    private String userPhone;

    private String address;

    private String location;

    private String creationTimestamp;

    private String desiredCompletionDate;

    private Integer amount;

    private boolean editable;
}
