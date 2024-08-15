package ua.corporation.memeclimb.entity.main.dto;

import lombok.Getter;
import lombok.Setter;
import ua.corporation.memeclimb.entity.main.Pool;

import java.util.UUID;

@Getter
@Setter
public class StepDto {
    private UUID id;
    private Integer number;
    private String textNumber;
    private double priceChance;
    private String secondMessage;
    private String thirdMessage;
    private Pool pool;

    public String displayStep() {
        return "<b>\uD83E\uDE99 Step " + textNumber + "</b>\n" +
                "<b>  - Top Prize Chance: " + priceChance + "%</b>\n" +
                "  - " + secondMessage +
                (thirdMessage.isEmpty() ? "" : "\n" + "<b> - " + thirdMessage + "</b>");
    }

    public String displayCurrentStep() {
        return "\uD83E\uDE99 Step " + textNumber + "<b> ⬅️[You are here] ⬅️</b>\n" +
                "  - Top Prize Chance: " + priceChance + "% <b>Increased!</b>\n" +
                "  - You're getting there! \uD83C\uDFAF \n";
    }

}
