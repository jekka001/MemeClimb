package ua.corporation.memeclimb.entity.main.dto;

import lombok.Getter;
import lombok.Setter;
import ua.corporation.memeclimb.entity.main.Pool;
import ua.corporation.memeclimb.entity.main.User;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.UUID;

@Getter
@Setter
public class ParticipantDto {
    private UUID id;
    private Pool pool;
    private User user;
    private int userStep;
    private double spendMoney;
    private double userHasMoney;
    private boolean isFirstly = true;

    public void spend(double amount) {
        MathContext context = new MathContext(4, RoundingMode.HALF_UP);

        userHasMoney = new BigDecimal(userHasMoney - amount, context).doubleValue();
        spendMoney = new BigDecimal(spendMoney + amount, context).doubleValue();
    }
}
