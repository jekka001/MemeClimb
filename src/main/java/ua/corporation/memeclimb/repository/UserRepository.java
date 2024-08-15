package ua.corporation.memeclimb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.corporation.memeclimb.entity.main.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByName(String name);
    User findByTelegramId(String telegramId);
}
