package ua.corporation.memeclimb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.corporation.memeclimb.entity.main.Participant;
import ua.corporation.memeclimb.entity.main.Pool;
import ua.corporation.memeclimb.entity.main.User;
import ua.corporation.memeclimb.entity.main.Winner;

import java.util.UUID;

public interface WinnerRepository extends JpaRepository<Winner, UUID> {
    Winner getWinnerByPoolAndUser(Pool pool, User user);
}
