package com.example.moneymanager.service;

import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repositry.ProfileRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class AppUserDetailSerive implements UserDetailsService {
    //method is responsible for loading the profile from the database

    private final ProfileRepo profileRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        ProfileEntity profileEntity= profileRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usre Not Found With This Email..."+email));

        return User.builder()
                .username(profileEntity.getEmail())
                .password(profileEntity.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
