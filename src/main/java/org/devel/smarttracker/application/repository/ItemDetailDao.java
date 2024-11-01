package org.devel.smarttracker.application.repository;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import org.devel.smarttracker.application.entities.ItemDetail;
import org.devel.smarttracker.application.entities.QItemDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemDetailDao extends AbstractDao<ItemDetail, Long> {

    private static final QItemDetail itemDetail = QItemDetail.itemDetail;

    public ItemDetailDao(EntityManager entityManager) {
        super(ItemDetail.class, entityManager);
    }

    public List<ItemDetail> findAllByItemId(Long itemId, Integer limit) {
        JPAQuery<ItemDetail> sqlQuery = getQuery()
                .selectFrom(itemDetail)
                .where(itemDetail.item.id.eq(itemId))
                .orderBy(itemDetail.createdAt.desc());
        if (limit != null) {
            sqlQuery.limit(limit);
        }
        return sqlQuery.fetch();
    }

    public void deleteAllByItemId(Long itemId) {
        getQuery()
                .delete(itemDetail)
                .where(itemDetail.item.id.eq(itemId))
                .execute();
    }
}
