package com.teamsbot.repository;

import com.teamsbot.model.TeamsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TeamsUserRepository extends JpaRepository<TeamsUser, Long> {
    Optional<TeamsUser> findByEmail(String email);
    Optional<TeamsUser> findByTeamsUserId(String teamsUserId);
}
