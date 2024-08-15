package ua.corporation.memeclimb.entity.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "pool_and_coin")
public class PoolCoin {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pool_id")
    private Pool pool;
    @ManyToOne
    @JoinColumn(name = "coin_id")
    private Coin coin;

    private boolean topReward;
    private int usdPrize;
}
