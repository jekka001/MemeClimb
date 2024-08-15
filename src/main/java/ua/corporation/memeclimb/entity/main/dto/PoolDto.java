package ua.corporation.memeclimb.entity.main.dto;

import lombok.Getter;
import lombok.Setter;
import ua.corporation.memeclimb.comparator.PoolData;
import ua.corporation.memeclimb.entity.main.Participant;
import ua.corporation.memeclimb.entity.main.PoolCoin;
import ua.corporation.memeclimb.entity.main.Step;
import ua.corporation.memeclimb.entity.main.Winner;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class PoolDto extends PoolData implements Comparable<PoolDto> {
    private UUID id;
    private String name;
    private double initialFee;
    private double probabilityWin;
    private double ratioProbability;
    private double ratioFee;
    private Timestamp drawDate;
    private List<Participant> participants;
    private List<Winner> winners;
    private List<Step> steps;
    private List<PoolCoin> poolCoins;

    public String displayPool(int completedSteps) {
        String poolReward = generateTextPoolReward();
        return "<b>" + name + "</b>\n\n" +
                "<b>\uD83C\uDFC6 Top Prize\uD83C\uDFC6: " + getStringTopReward() + "</b>\n" +
                "<b>\uD83C\uDFC6✅ Pool Rewards:</b> " + poolReward + " (get one of)\n\n" +
                "<b>\uD83D\uDC65 Total participants: " + participants.size() + "</b>\n" +
                "\uD83C\uDFC6\uD83D\uDC64 Number of Winners: " + winners.size() + "\n" +
                "<b>\uD83D\uDC5FYour Completed Steps: " + completedSteps + "/" + steps.size() + "</b>\n";
    }

    public String generateTextPoolReward() {
        List<String> coinsName = poolCoins.stream()
                .filter(poolCoin -> !poolCoin.isTopReward())
                .map(poolCoin -> poolCoin.getCoin().getName()).toList();
        StringBuilder result = new StringBuilder();
        for (int counter = 0; counter < coinsName.size(); counter++) {
            result.append(coinsName.get(counter));
            if (counter != coinsName.size() - 1) {
                result.append(", ");
            }
        }
        return result.toString();
    }

    public List<PoolCoin> getPoolCoins() {
        return poolCoins.stream().filter(poolCoin -> !poolCoin.isTopReward()).collect(Collectors.toList());
    }

    public PoolCoin getTopReward() {
        return poolCoins.stream()
                .filter(PoolCoin::isTopReward)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("initialize top reward"));
    }

    public String getStringTopReward() {
        PoolCoin topReward = getTopReward();

        return topReward.getCoin().getSymbol() + " ($" + topReward.getUsdPrize() + ")";
    }


    @Override
    public int compareTo(PoolDto pool) {
        return compare(this, pool);
    }
}
