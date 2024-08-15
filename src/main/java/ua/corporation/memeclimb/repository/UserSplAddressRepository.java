package ua.corporation.memeclimb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.corporation.memeclimb.entity.main.Coin;
import ua.corporation.memeclimb.entity.main.User;
import ua.corporation.memeclimb.entity.main.UserSplAddress;

import java.util.UUID;

public interface UserSplAddressRepository extends JpaRepository<UserSplAddress, UUID> {

    UserSplAddress getUserSplAddressByUserAndCoin(User user, Coin coin);
}
