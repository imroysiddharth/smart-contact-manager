package com.scm.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositories.UserRepo;
import com.scm.services.UserService;


@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepo userRepo ;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        //Create user ID
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        //Password Encoding
        System.out.println("password is"+ user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
            
        //**************************
      
           // Enable user
            user.setEnabled(true);
        // ******************

        //Set the user role'
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        return userRepo.save(user);
    }

    @Override
    public Optional<User> getUserById(String id) {
       return userRepo.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
        User oldUser = userRepo.findById(user.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User Not Found !"));
         oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());
        oldUser.setPassword(user.getPassword());
        oldUser.setAbout(user.getAbout());
        oldUser.setPhoneNumber(user.getPhoneNumber());
        oldUser.setProfilePic(user.getProfilePic());
        oldUser.setEnabled(user.isEnabled());
        oldUser.setEmailVerified(user.isEmailVerified());
        oldUser.setPhoneVerified(user.isPhoneVerified());
        oldUser.setProvider(user.getProvider());
        oldUser.setProviderUserId(user.getProviderUserId());
        // save the user in database
        User save = userRepo.save(oldUser);
        return Optional.ofNullable(save);
    }

    @Override
    public void deleteUser(String id) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User Not Found !"));
        userRepo.delete(user);
    }

    @Override
    public boolean isUserExist(String userId) {
         User user = userRepo.findById(userId).orElse(null);
         return user!=null ? true : false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
          User user = userRepo.findByEmail(email).orElse(null);
          return user!=null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
       return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
         User user = userRepo.findByEmail(email).orElse(null);
         return user;
    }

}
