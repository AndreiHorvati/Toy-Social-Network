package com.example.toysocialnetworkgui.model.validators;

import com.example.toysocialnetworkgui.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Clasa care se ocupa de validarea unui utilizator
 */
public class UserValidator implements Validator<User> {
    /**
     * Valideaza un user
     * @param entity - entitatea care terbuie validata
     * @throws UserValidationException daca userul nu este valid
     */
    @Override
    public void validate(User entity) {
        List<String> problems = new ArrayList<String>();
        StringBuilder problemsString = new StringBuilder();

        for(int i=0;i<entity.getFirstName().length();i++) {
            if(!Character.isLetter(entity.getFirstName().charAt(i))) {
                problems.add("Prenumele utilizatorului trebuie sa contina doar litere!");
                break;
            }
        }

        for(int i=0;i<entity.getLastName().length();i++) {
            if(!Character.isLetter(entity.getLastName().charAt(i))) {
                problems.add("Numele utilizatorului trebuie sa contina doar litere!");
                break;
            }
        }

        if(problems.size() != 0) {
            for (int i = 0; i < problems.size() - 1; i++) {
                problemsString.append(problems.get(i)).append("\n");
            }
            problemsString.append(problems.get(problems.size() - 1));
        }

        if(!problemsString.toString().equals("")) {
            throw new UserValidationException(problemsString.toString());
        }
    }
}
