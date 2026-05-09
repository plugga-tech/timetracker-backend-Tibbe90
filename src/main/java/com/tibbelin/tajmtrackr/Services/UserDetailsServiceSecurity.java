package com.tibbelin.tajmtrackr.Services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tibbelin.tajmtrackr.dto.UserDTO;
import com.tibbelin.tajmtrackr.models.User;


@Service
class UserDetailsServiceSecurity implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceSecurity(UserService userService) {
        this.userService = userService;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("username not found: " + username);
        }
        return new UserDTO(user);
    }

    
}