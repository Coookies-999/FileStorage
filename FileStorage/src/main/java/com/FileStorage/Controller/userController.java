package com.FileStorage.Controller;


import com.FileStorage.Dto.userLoginDto;
import com.FileStorage.Dto.userResponseDto;
import com.FileStorage.Dto.userSignupDto;
import com.FileStorage.Model.User;
import com.FileStorage.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class userController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> createNewUser(@RequestBody @Valid userSignupDto user){
            return userService.createUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<userLoginDto> login(@RequestBody @Valid userLoginDto user){
        return userService.signin(user);
    }

    @GetMapping ("/getusers")
    public ResponseEntity<List<User>> getAllUsers(){

        return ResponseEntity.ok(userService.getAllUsers());
    }


    @GetMapping("/Test")
    public String Testing(){
        return "Worked";
    }
}
