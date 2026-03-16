package com.phatakp.ipl_services.users.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDTO {
    private String clerkId;
    private String firstName;
    private String lastName;
    private String team;
    private Boolean isActive;

    public Boolean equals(UserShortDTO u) {
        return this.clerkId.equals(u.clerkId);
    }
}
