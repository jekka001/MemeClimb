package ua.corporation.memeclimb.entity.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String telegramId;
    @Column(name = "nothing")
    private byte[] privateKey;
    @Column(name = "nothing_a_lot")
    private String publicKey;
    private int page;
    private UUID chosenPoolId;
    private boolean initializedWithdraw;
    private boolean deactivate;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Participant> participants;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Winner> winPool;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<UserSplAddress> userSplAddresses;

    public User(String name, String telegramId) {
        this.name = name;
        this.telegramId = telegramId;
    }
}
