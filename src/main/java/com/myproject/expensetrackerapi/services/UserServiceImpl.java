package com.myproject.expensetrackerapi.services;

import com.myproject.expensetrackerapi.domain.User;
import com.myproject.expensetrackerapi.exceptions.EtAuthException;
import com.myproject.expensetrackerapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    Pattern emailPattern;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        emailPattern = Pattern.compile("^(.+)@(.+)$");
    }

    @Override
    public User validateUser(String email, String password) throws EtAuthException {
        return null;
    }

    @Override
    public User registerUser(String firstName, String lastName, String email, String password) throws EtAuthException {
        if (email != null) email = email.toLowerCase();

        if (!emailPattern.matcher(email).matches())  throw new EtAuthException("Invalid email format");

        Integer count = userRepository.getCountByEmail(email);
        if (count > 0) throw new EtAuthException("Email already in use");

        Integer userId = userRepository.create(firstName, lastName, email, password);
        return userRepository.findById(userId);
    }

}
