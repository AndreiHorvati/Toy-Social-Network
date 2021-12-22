package com.example.toysocialnetworkgui.service;

import com.example.toysocialnetworkgui.model.User;
import com.example.toysocialnetworkgui.repository.database.UserDatabaseRepository;
import com.example.toysocialnetworkgui.utils.Constants;
import com.example.toysocialnetworkgui.utils.PasswordHasher;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class AuthenticationService {
    private User currentUser;
    private UserDatabaseRepository repository;

    public AuthenticationService(UserDatabaseRepository repository) {
        this.repository = repository;
        currentUser = null;
    }

    public void login(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PasswordHasher passwordHasher = new PasswordHasher();

        String storedPassword = repository.getPasswordByUsername(username);

        if(storedPassword == null) {
            throw new NonExistingUserException(Constants.nonExistingUserExceptionMessage);
        }

        if (passwordHasher.validatePassword(password, repository.getPasswordByUsername(username))) {
            currentUser = repository.getUserByUsername(username);
        } else {
            throw new WrongPasswordException(Constants.wrongPasswordException);
        }
    }
}
