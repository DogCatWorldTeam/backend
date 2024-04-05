package com.techeer.abandoneddog.bookmark.repository;

import com.techeer.abandoneddog.bookmark.entity.Bookmark;
import com.techeer.abandoneddog.pet_board.entity.PetBoard;
import com.techeer.abandoneddog.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Page<Bookmark> findBookmarksByUserIdAndIsDeletedFalse(Pageable pageable, Long userId);

    boolean existsByPetBoardAndUser(PetBoard petBoard, Users user);

    Bookmark findByPetBoardAndUser(PetBoard petBoard, Users users);

    @Modifying
    @Query("UPDATE Bookmark b SET b.isDeleted=false WHERE b.id= :bookmarkId")
    void resave(Long bookmarkId);
}
