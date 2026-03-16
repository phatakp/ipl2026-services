package com.phatakp.ipl_services.users.controllers;

import com.phatakp.ipl_services.users.dtos.UserFormDTO;
import com.phatakp.ipl_services.users.services.UserService;
import com.phatakp.ipl_services.users.dtos.UserDTO;
import com.phatakp.ipl_services.users.dtos.UserRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Module")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Create or Update User Profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not Authorized", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data in input", content = @Content),
    })
    @PostMapping
    public ResponseEntity<UserDTO> upsertUser(
            @Valid @RequestBody UserRequestDTO request
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        userService.assertIsOwnerAction(authentication.getName(), request.getClerkId(), "create user profile");
        return ResponseEntity.ok(userService.upsertUser(request));
    }

    @Operation(summary = "Activate User Profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data in input", content = @Content),
    })
    @PutMapping("/activate/{userId}")
    public ResponseEntity<Void> activateUser(
            @Parameter(description = "ID of the user to be retrieved",required = true)
            @PathVariable String userId
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        userService.assertIsAdminAction(authentication.getName(), "activate user profile");
        userService.activateUser(userId);
        return ResponseEntity.ok().build();
    }




    @Operation(summary = "Get All users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not Authorized", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content),
    })
    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @Operation(summary = "Get user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content),
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID of the user to be retrieved",required = true)
            @PathVariable String userId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(summary = "Get current logged in user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content),
    })
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.getUserById(authentication.getName()));
    }


    @Operation(summary = "Get user form by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserFormDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content),
    })
    @GetMapping("/form/{userId}")
    public ResponseEntity<List<UserFormDTO>> getUserForm(
            @Parameter(description = "ID of the user to be retrieved",required = true)
            @PathVariable String userId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.getUserForm(userId));
    }
}
