package com.bitter.backendapi;

import org.springframework.stereotype.Service;


@Service
public class UserService {

    public boolean validate(User user, String password){
        return password.equals(user.getPassword());
    }

}
