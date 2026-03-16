package com.phatakp.ipl_services.matches.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchStatDTO {
    String team;
    Short totalAmount;
}
