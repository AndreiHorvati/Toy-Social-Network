package com.example.toysocialnetworkgui.repository.file;

import com.example.toysocialnetworkgui.model.User;
import com.example.toysocialnetworkgui.model.validators.*;

import java.util.List;

/**
 * Repository care pastreaza useri in memorie
 * Extinde clasa abstracta AbstractFileRepository
 */
public class UserRepository extends AbstractFileRepository<Long, User> {
    /**
     * Contructorul repository-ului
     * @param validator - validatorul userilor
     * @param file - fisierul in care sunt salvati userii
     */
    public UserRepository(Validator<User> validator, String file) {
        super(validator, file);
    }

    @Override
    protected User extractEntity(List<String> attributes) {
        User user = new User(attributes.get(1), attributes.get(2));
        user.setId(Long.parseLong(attributes.get(0)));

        return user;
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId() + "," + entity.getFirstName() + "," + entity.getLastName();
    }
}
