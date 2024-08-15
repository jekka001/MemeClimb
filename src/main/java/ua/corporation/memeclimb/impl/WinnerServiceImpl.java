package ua.corporation.memeclimb.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.corporation.memeclimb.entity.main.Pool;
import ua.corporation.memeclimb.entity.main.User;
import ua.corporation.memeclimb.entity.main.Winner;
import ua.corporation.memeclimb.repository.WinnerRepository;
import ua.corporation.memeclimb.service.WinnerService;

@Service
@RequiredArgsConstructor
public class WinnerServiceImpl implements WinnerService {
    private final WinnerRepository repository;

    @Override
    public Winner save(Pool pool, User user) {
        Winner winner = repository.getWinnerByPoolAndUser(pool, user);

        if (winner == null) {
            winner = new Winner();

            winner.setPool(pool);
            winner.setUser(user);

            return repository.saveAndFlush(winner);
        }

        return winner;
    }

    @Override
    public void delete() {
        repository.deleteAll();
    }
}
