package ua.corporation.memeclimb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.corporation.memeclimb.entity.main.Coin;

import java.util.Optional;
import java.util.UUID;

public interface CoinRepository extends JpaRepository<Coin, UUID> {
    Optional<Coin> findCoinBySymbol(String symbol);
    Optional<Coin> getCoinByMainIsTrue();
}
