package ua.corporation.memeclimb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.corporation.memeclimb.entity.main.Participant;
import ua.corporation.memeclimb.entity.main.Pool;
import ua.corporation.memeclimb.entity.main.User;

import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
    Participant getParticipantByPoolAndUser(Pool pool, User user);
}
