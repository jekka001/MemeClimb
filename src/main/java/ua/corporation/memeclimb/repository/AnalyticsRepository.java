package ua.corporation.memeclimb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.corporation.memeclimb.entity.main.Analytics;

import java.util.Optional;
import java.util.UUID;

public interface AnalyticsRepository extends JpaRepository<Analytics, UUID> {
}
