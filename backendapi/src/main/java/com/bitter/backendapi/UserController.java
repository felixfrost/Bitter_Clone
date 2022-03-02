package com.bitter.backendapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/userid/{id}")
    User getUser(@PathVariable("id") long id){
        return userRepository.findById(id).orElse(new User());
    }

    @GetMapping("/username/{username}")
    User getUser(@PathVariable("username") String username){
        return userRepository.findByUsername(username);
    }

    @GetMapping("/users")
    List<User> getUsers(){
        return userRepository.findAll();
    }


    @PostMapping("/adduserobj")
    User addUserObj(@RequestBody User user){
        System.out.println("Called");
        return userRepository.save(user);
    }

    @PostMapping("/validate")
    boolean loginUser(@RequestBody LoginForm loginForm){
        if (userRepository.findByUsername(loginForm.getUsername()) == null)
            return false;
        return userService.validate(userRepository.findByUsername(loginForm.getUsername()), loginForm.getPassword());
    }

    @PostMapping("/deleteuser")
    void deleteUser(@RequestBody User user){
        userRepository.delete(user);
    }





}
