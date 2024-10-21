package ua.corporation.memeclimb.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.corporation.memeclimb.entity.main.Analytics;
import ua.corporation.memeclimb.repository.AnalyticsRepository;
import ua.corporation.memeclimb.service.AnalyticsService;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {
    private final AnalyticsRepository repository;

    @Override
    public void save(Analytics analytics) {
        repository.saveAndFlush(analytics);
    }
}
