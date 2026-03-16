package com.phatakp.ipl_services.users.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String clerkId;
    private String email;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private String role;
    private String team;
    private Float balance;
    private Short doublesLeft;
    private Long rank;
    private Boolean isActive;
}
