package ua.corporation.memeclimb.entity.main;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "step")
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Integer number;
    private String textNumber;
    private double priceChance;
    private String secondMessage;
    private String thirdMessage;
    @ManyToOne
    @JoinColumn(name = "pool_id")
    private Pool pool;

    public String displayStep() {
        return "<b>\uD83E\uDE99 Step " + textNumber + "</b>\n" +
                "of total " + pool.getSteps().size() + " Levels in the " + pool.getName() + "\n" +
                "<b>  - Top Prize Chance: " + priceChance + "%</b>\n" +
                "  - " + secondMessage +
                (thirdMessage.isEmpty() ? "" : "\n" + "<b> - " + thirdMessage + "</b>");
    }

    public String displayCurrentStep() {
        return "\uD83E\uDE99 Step " + textNumber + "<b> ⬅️[You are here] ⬅️</b>\n" +
                "of total " + pool.getSteps().size() + " Levels in the " + pool.getName() + "\n" +
                "  - Top Prize Chance: " + priceChance + "% <b>Increased!</b>\n" +
                "  - You're getting there! \uD83C\uDFAF \n";
    }
}
