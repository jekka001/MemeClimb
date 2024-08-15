package ua.corporation.memeclimb.service;

import ua.corporation.memeclimb.entity.main.Pool;
import ua.corporation.memeclimb.entity.main.User;
import ua.corporation.memeclimb.entity.main.Winner;

public interface WinnerService {
    Winner save(Pool pool, User user);
    void delete();
}
