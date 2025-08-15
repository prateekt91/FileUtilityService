package com.prat.fileutility.controller;

import com.prat.fileutility.exception.InvalidFileException;
import com.prat.fileutility.model.Users;
import com.prat.fileutility.repository.UsersControlRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Operations", description = "API endpoints for user management operations")
public class UserController {


    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private UsersControlRepository usersControlRepository;

    public UserController(UsersControlRepository usersControlRepository) {
        this.usersControlRepository = usersControlRepository;
    }

    @Operation(summary = "Get user details", description = "Retrieves user details by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user details", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Users.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request - username is empty"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving user details")
    })
    @GetMapping("/getUser")
    public ResponseEntity<Users> getUserDetails(
            @Parameter(description = "Username to retrieve details for", required = true, example = "johndoe")
            @RequestParam String userName) {
        log.info("Inside UserController.getUserDetails() method");
        
        // Validate userName parameter
        if (!StringUtils.hasText(userName)) {
            throw new InvalidFileException("User name cannot be empty");
        }
        
        return ResponseEntity.ok(usersControlRepository.findByUserName(userName));
    }
}
