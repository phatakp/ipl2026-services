package com.phatakp.ipl_services.users.dtos;

import com.phatakp.ipl_services.predictions.models.PredictionStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFormDTO {
    private String clerkId;
    private String status;
}
