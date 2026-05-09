package com.tibbelin.tajmtrackr.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibbelin.tajmtrackr.Services.UserService;
import com.tibbelin.tajmtrackr.dto.LoginDTO;
import com.tibbelin.tajmtrackr.dto.UserDurationsDTO;
import com.tibbelin.tajmtrackr.models.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }
    
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/admin") 
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDurationsDTO>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUserDurations());
    }

    @PostMapping("/login") 
    public ResponseEntity<?> login(@RequestBody LoginDTO credentials, HttpServletRequest sessionRequest){
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    credentials.getUsername(), credentials.getPassword())
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                HttpSession newSession = sessionRequest.getSession(true);
                newSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

                User user = userService.getUserByUsername(authentication.getName());
                return ResponseEntity.ok(user);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Invalid username of password");
        }
    }
}
