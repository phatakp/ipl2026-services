package com.phatakp.ipl_services.users.dtos;

import com.phatakp.ipl_services.teams.models.TeamEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    @NotNull
    @NotBlank
    private String clerkId;

    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String firstName;

    @NotNull
    @NotBlank
    private String lastName;

    private String imageUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TeamEnum team;

    private Float balance=0.00f;

    private Short doublesLeft=5;

    private Boolean isActive=false;
}
