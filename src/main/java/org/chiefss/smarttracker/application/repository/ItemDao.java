package org.chiefss.smarttracker.application.repository;

import jakarta.persistence.EntityManager;
import org.chiefss.smarttracker.application.entities.Item;
import org.chiefss.smarttracker.application.entities.QItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemDao extends AbstractDao<Item, Long> {

    private static final QItem item = QItem.item;

    public ItemDao(EntityManager entityManager) {
        super(Item.class, entityManager);
    }

    public List<Item> findAllActivated() {
        return getQuery()
                .selectFrom(item)
                .where(item.deletedAt.isNull())
                .orderBy(item.createdAt.asc(), item.name.asc())
                .fetch();
    }
}
