package ua.corporation.memeclimb.comparator;

import ua.corporation.memeclimb.entity.main.dto.PoolDto;

import java.util.Comparator;

public class PoolData implements Comparator<PoolDto> {
    @Override
    public int compare(PoolDto pool, PoolDto pool2) {
        return pool2.getDrawDate().compareTo(pool.getDrawDate());
    }
}
