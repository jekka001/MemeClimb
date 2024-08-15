package ua.corporation.memeclimb.entity.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coin")
public class Coin {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String symbol;
    private int decimals;
    private double amount;
    private double amountRaw;
    private String mint;
    private boolean main;

    @JsonIgnore
    @OneToMany(mappedBy = "coin", fetch = FetchType.EAGER)
    private List<UserSplAddress> userSplAddresses;
}
