package ua.corporation.memeclimb.entity.main.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinDto {
    private UUID id;
    private String name;
    private String symbol;
    private int decimals;
    private double amount;
    private long amountRaw;
    private String mint;
    private boolean main;

    public void setAmount(double amount) {
        MathContext context = new MathContext(4, RoundingMode.HALF_DOWN);

        this.amount = new BigDecimal(amount, context).doubleValue();
    }

    public double getAmountByAmountRaw() {
        return (amountRaw * 1.0) / getDecimalMultiplayer();
    }

    public long getAmountRowByAmount() {
        return amountRaw * getDecimalMultiplayer();
    }

    public void setAmountRawByAmount(double amount) {
        this.amountRaw = Double.valueOf(amount * getDecimalMultiplayer()).longValue();
    }

    public void setAmountRaw(long amountRaw) {
        this.amountRaw = amountRaw;
    }

    public long getDecimalMultiplayer() {
        long result = 1;

        for (int counter = 0; counter < decimals; counter++) {
            result *= 10;
        }

        return result;
    }
}
