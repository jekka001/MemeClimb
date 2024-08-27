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
