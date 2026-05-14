package com.example.moneymanager.service;

import com.example.moneymanager.dto.AuthDTO;
import com.example.moneymanager.dto.ProfileDTO;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repositry.ProfileRepo;
import com.example.moneymanager.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService { //it includes methods for creating updating deleting and retrieving the profiles

    private final ProfileRepo profileRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Value("${app.activation-url:}")
    private String activationUrl;

    public ProfileDTO registerProfile(ProfileDTO profileDTO){

        ProfileEntity newProfile= toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());

        newProfile=profileRepo.save(newProfile);

        profileDTO= toDTO(newProfile);

        //send activation mail
        String activationLink = buildActivationLink(newProfile.getActivationToken());
        log.info("Activation link generated for email={} token={} link={}",
                newProfile.getEmail(), newProfile.getActivationToken(), activationLink);
        String subject="Activate your Money Manager Account jaldi jaldi...";
        String body="Click on the following link to activate your account--->"+activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject,body);

        return profileDTO;
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO){
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullname(profileDTO.getFullname())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullname(profileEntity.getFullname())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();

    }

    public boolean activateProfile(String activationToken){
        String token = activationToken == null ? "" : activationToken.trim();
        log.info("Activation requested with token={}", token);
        return profileRepo.findByActivationToken(token)
                .map(profileEntity -> {
                    log.info("Activation token matched email={}", profileEntity.getEmail());
                    profileEntity.setActive(true);
                    profileEntity.setActivationToken(null);
                    profileRepo.save(profileEntity);
                    return true;
                }).orElseGet(() -> {
                    log.warn("Activation token not found token={}", token);
                    return false;
                });
    }

    public boolean isAccountActive(String email){
        return profileRepo.findByEmail(email)
                .map(ProfileEntity::getActive)
                .orElse(false);
    }

    public ProfileEntity getCuurentAccount(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        return profileRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with this email-->"+authentication.getName()));
    }


    public ProfileDTO  getPublicProfile(String email){
        ProfileEntity profile= null;
        if(email==null){
            profile=getCuurentAccount();
        }else{
           profile= profileRepo.findByEmail(email)
                    .orElseThrow(()-> new UsernameNotFoundException("User Not found With This email-->"+email));
        }

        return toDTO(profile);
    }


    public Map<String, Object> authticateAndGenerateToken(AuthDTO authDTO) {
        try{
             authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(),authDTO.getPassword()));
             return Map.of(
                     "token", jwtUtil.generateToken(authDTO.getEmail()),
                     "user", getPublicProfile(authDTO.getEmail())
             );
        }catch (Exception e){
            throw  new RuntimeException("Invalid UserName and Password");
        }
    }

    private String buildActivationLink(String activationToken) {
        if (StringUtils.hasText(activationUrl)) {
            return ServletUriComponentsBuilder
                    .fromUriString(activationUrl)
                    .queryParam("token", activationToken)
                    .toUriString();
        }

        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/activate")
                .queryParam("token", activationToken)
                .toUriString();
    }
}

