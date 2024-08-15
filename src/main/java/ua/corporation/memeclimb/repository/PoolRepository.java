package ua.corporation.memeclimb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.corporation.memeclimb.entity.main.Pool;

import java.util.List;
import java.util.UUID;

public interface PoolRepository extends JpaRepository<Pool, UUID> {
    @Query("SELECT pool FROM Pool AS pool ORDER BY pool.drawDate DESC")
    Page<Pool> getPools(Pageable pageable);
}
