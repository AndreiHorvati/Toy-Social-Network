package com.example.toysocialnetworkgui.repository.file;

import com.example.toysocialnetworkgui.model.Friendship;
import com.example.toysocialnetworkgui.model.Tuple;
import com.example.toysocialnetworkgui.model.validators.Validator;

import java.util.List;

/**
 * Repository care pastreaza prietenii in memorie
 * Extinde clasa abstracta AbstractFileRepository
 */
public class FriendshipRepository extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {
    /**
     * Contructorul repository-ului
     * @param validator - validatorul prieteniilor
     * @param file - fisierul in care sunt salvate prieteniile
     */
    public FriendshipRepository(Validator<Friendship> validator, String file) {
        super(validator, file);
    }

    @Override
    protected Friendship extractEntity(List<String> attributes) {
        Friendship friendship = new Friendship();
        Long id1 = Long.parseLong(attributes.get(0));
        Long id2 = Long.parseLong(attributes.get(1));
        Tuple<Long, Long> id = new Tuple<>(id1, id2);

        friendship.setId(id);

        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId().getLeft() + "," + entity.getId().getRight();
    }

    @Override
    public Friendship save(Friendship entity) {
        Long id1 = entity.getId().getLeft();
        Long id2 = entity.getId().getRight();

        if (findOne(new Tuple<>(id1, id2)) == null && findOne(new Tuple<>(id2, id1)) == null) {
            return super.save(entity);
        }

        return entity;
    }
}
