package ua.corporation.memeclimb.entity.main;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.corporation.memeclimb.comparator.PoolData;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pool")
public class Pool {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private double initialFee;
    private double probabilityWin;
    private double ratioProbability;
    private double ratioFee;
    private Timestamp drawDate;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pool")
    private List<Participant> participants;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pool")
    private List<Winner> winners;

    @OneToMany(mappedBy = "pool", fetch = FetchType.EAGER)
    private List<Step> steps;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pool")
    private List<PoolCoin> poolCoins;
}
