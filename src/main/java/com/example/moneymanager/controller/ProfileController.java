package com.example.moneymanager.controller;

import com.example.moneymanager.dto.AuthDTO;
import com.example.moneymanager.dto.ProfileDTO;
import com.example.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController { // this class will handle login registration and update profiles etc

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile( @RequestBody ProfileDTO profileDTO){
        ProfileDTO registerprofile= profileService.registerProfile(profileDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(registerprofile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        boolean isActivate= profileService.activateProfile(token);
        if (isActivate) return ResponseEntity.ok("Yeyy.. Teri Id Active Ho Gyi Majjje Kar...");
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nahi Bani Teri Id BhuutniKe...");
    }

    @PostMapping("/login")
    public  ResponseEntity<Map<String,Object>> login(@RequestBody AuthDTO authDTO){
        try{
            if(!profileService.isAccountActive(authDTO.getEmail())){
                return  ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message" ,"The Account is Not Active , Pehle Registor karle Laadle"
                ));
            }
            Map<String,Object> response= profileService.authticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


}
