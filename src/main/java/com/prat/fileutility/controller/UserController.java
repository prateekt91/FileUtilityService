package com.prat.fileutility.controller;

import com.prat.fileutility.model.Users;
import com.prat.fileutility.repository.UsersControlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private UsersControlRepository usersControlRepository;

    public UserController(UsersControlRepository usersControlRepository) {
        this.usersControlRepository = usersControlRepository;
    }

    @GetMapping("/getUser")
    public ResponseEntity<Users> getUserDetails(@RequestParam String userName) {
        log.info("Inside UserController.getUserDetails() method");
        return ResponseEntity.ok(usersControlRepository.findByUserName(userName));
    }
}
