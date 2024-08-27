package ua.corporation.memeclimb.impl;

import lombok.RequiredArgsConstructor;
import org.p2p.solanaj.core.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.corporation.memeclimb.entity.main.User;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.mapper.UserMapper;
import ua.corporation.memeclimb.repository.UserRepository;
import ua.corporation.memeclimb.service.UserService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Random random = new Random();
    @Value("${meme.climb}")
    private String phrase;
    @Value("${server.name}")
    private String serverName;
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserDto getServerWallet() {
        Optional<User> user = repository.findByName(serverName);
        if (user.isEmpty()) {
            UserDto server = new UserDto(serverName, serverName);

            return createServer(server);
        }
        return mapper.toDto(user.get());
    }

    private UserDto createServer(UserDto user) {
        Map<byte[], String> keys = createKeys(user);

        user.setPublicKey(getPublicKey(keys));
        user.setPrivateKey(getPrivateKey(keys));

        return save(user);
    }

    @Override
    public UserDto create(com.pengrad.telegrambot.model.User telegramUser) {
        String name = telegramUser.username() != null ? telegramUser.username() : telegramUser.firstName();
        UserDto user = new UserDto(name, String.valueOf(telegramUser.id()));
        Map<byte[], String> keys = createKeys(user);

        user.setPublicKey(getPublicKey(keys));
        user.setPrivateKey(getPrivateKey(keys));

        return save(user);
    }

    private Map<byte[], String> createKeys(UserDto user) {
        Account account = createAccount(user);

        return Collections.singletonMap(
                account.getSecretKey(),
                account.getPublicKey().toString()
        );
    }

    private Account createAccount(UserDto user) {
        List<String> words = Arrays.asList(
                user.getName(),
                user.getTelegramId(),
                generateRandomPhrase()
        );

        return Account.fromBip44Mnemonic(words, generateRandomPhrase());
    }

    private String generateRandomPhrase() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int counter = 0; counter < phrase.length(); counter++) {
            int randomNumber = random.nextInt(phrase.length());
            stringBuilder.append(phrase.charAt(randomNumber));
        }

        return stringBuilder.toString();
    }

    private String getPublicKey(Map<byte[], String> keys) {
        return keys.values().stream().findFirst().orElseThrow(() -> new RuntimeException("problem with keys"));
    }

    private byte[] getPrivateKey(Map<byte[], String> keys) {
        return keys.keySet().stream().findFirst().orElseThrow(() -> new RuntimeException("problem with keys"));
    }

    @Override
    public List<UserDto> get() {
        List<User> users = repository.findAll();
        return mapper.toListDto(users);
    }

    @Override
    public UserDto get(com.pengrad.telegrambot.model.User telegramUser) {
        String telegramId = String.valueOf(telegramUser.id());
        User user = repository.findByTelegramId(telegramId);

        if (user == null) {
            return create(telegramUser);
        }

        return mapper.toDto(user);
    }

    @Override
    public UserDto get(UUID userId) {
        User user = repository.findById(userId).orElseThrow(() -> new RuntimeException("User is not exist"));

        return mapper.toDto(user);
    }

    @Override
    public UserDto save(UserDto userDto) {
        User user = mapper.toEntity(userDto);
        User savedUser = repository.saveAndFlush(user);

        return mapper.toDto(savedUser);
    }

    @Override
    public UserDto saveChosenPool(String poolId, UserDto userDto) {
        User user = mapper.toEntity(userDto);
        UUID poolUUID = UUID.fromString(poolId);
        user.setChosenPoolId(poolUUID);

        User savedUser = repository.saveAndFlush(user);

        return mapper.toDto(savedUser);
    }

    @Override
    public void delete() {
        repository.deleteAll();
    }
}
