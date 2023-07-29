package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.user.model.UserEntity;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequestEntity, Long> {
    @Query("SELECT itreq " +
            " FROM ItemRequestEntity AS itreq " +
            " JOIN FETCH itreq.requestor " +
            "WHERE itreq.requestor = ?1 " +
            "ORDER BY itreq.created " +
            " DESC")
    List<ItemRequestEntity> findAllItemsRequestsByOwner(UserEntity requestor);
}
