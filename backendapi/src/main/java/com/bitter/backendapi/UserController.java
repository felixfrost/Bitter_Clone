package com.bitter.backendapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepo;

    @GetMapping("/userid/{id}")
    User getUser(@PathVariable("id") long id){
        return userRepo.getUserById(id);
    }

    @GetMapping("/username/{username}")
    User getUser(@PathVariable("username") String username){
        return userRepo.getUserByUsername(username);
    }

    @GetMapping("/users")
    List<User> getUsers(){
        return userRepo.getAllUsers();
    }

    @PostMapping("/adduser")
    void addUser(@RequestBody User user){
        userRepo.addUser(user);
    }

    @PostMapping("/deleteuser")
    void deleteUser(@RequestBody User user){
        userRepo.deleteUserById(user.getId());
    }




}