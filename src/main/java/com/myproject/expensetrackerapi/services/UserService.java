package com.myproject.expensetrackerapi.services;

import com.myproject.expensetrackerapi.domain.User;
import com.myproject.expensetrackerapi.exceptions.EtAuthException;

public interface UserService {

    User validateUser(String email, String password) throws EtAuthException;

    User registerUser(String firstName, String lastName, String email, String password) throws EtAuthException;


}
