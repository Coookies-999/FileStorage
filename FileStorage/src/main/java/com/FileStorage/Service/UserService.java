package com.FileStorage.Service;

import com.FileStorage.Dto.userLoginDto;
import com.FileStorage.Dto.userResponseDto;
import com.FileStorage.Dto.userSignupDto;
import com.FileStorage.Model.User;
import com.FileStorage.Repository.userRepository;
import com.FileStorage.Security.JwtService;
import com.FileStorage.config.PasswordConfig;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private userRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public ResponseEntity<User> createUser(userSignupDto userSignupDto){

        //Check if user exists with provided email
        Optional<User> users=userRepository.findByEmail(userSignupDto.getEmail());
        if(users.isPresent()){
            throw new IllegalArgumentException("User with "+ userSignupDto.getEmail()+" exists, Please choose  different Email");
        }

        //If not exists create user, generate jwt and hash the password and store in db
        UUID id=UUID.randomUUID();
        User user=User.builder()
                .userId(id.toString())
                .email(userSignupDto.getEmail())
                .name(userSignupDto.getName())
                .hashedPass(passwordEncoder.encode(userSignupDto.getPassword()))  //Hashing password
                .createdAt(LocalDate.now())
                .build();
        userRepository.saveUser(user);
        String token = jwtService.generateToken(user.getUserId());

        return ResponseEntity.ok()
                .header("Jwt-Token",token)
                .body(user);
    }

    public List<User> getAllUsers(){
        return userRepository.getUsers();
    }

    public ResponseEntity<userLoginDto> signin( userLoginDto user) {
        Optional<User> users=userRepository.findByEmail(user.getEmail());

        //If user doesn't exist
        if(users.isEmpty())
            throw  new IllegalArgumentException("user doesn't exist");

        String pass=user.getPassword();

        String hashedPassword=users.get().getHashedPass();
        //If password mismatches
        if(!BCrypt.checkpw(pass,hashedPassword)){
            throw new IllegalArgumentException("Password Mismatch");
        }
        String token = jwtService.generateToken(users.get().getUserId());
        return ResponseEntity.ok().header("Jwt-Token",token).body(user);
    }

}
