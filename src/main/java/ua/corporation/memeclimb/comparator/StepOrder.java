package ua.corporation.memeclimb.comparator;

import ua.corporation.memeclimb.entity.main.Step;

import java.util.Comparator;

public class StepOrder implements Comparator<Step> {
    @Override
    public int compare(Step step, Step step2) {
        return step.getNumber() - step2.getNumber();
    }
}
