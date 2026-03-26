package com.ramis.progresstracker.repository;

import com.ramis.progresstracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.level = :level ORDER BY u.totalXP DESC")
    List<User> findUsersByLevel(@Param("level") Integer level);

}
