package ua.corporation.memeclimb.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.corporation.memeclimb.entity.main.Participant;
import ua.corporation.memeclimb.entity.main.Pool;
import ua.corporation.memeclimb.entity.main.User;
import ua.corporation.memeclimb.entity.main.dto.ParticipantDto;
import ua.corporation.memeclimb.entity.main.dto.PoolDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.mapper.ParticipantMapper;
import ua.corporation.memeclimb.mapper.PoolMapper;
import ua.corporation.memeclimb.mapper.UserMapper;
import ua.corporation.memeclimb.repository.ParticipantRepository;
import ua.corporation.memeclimb.service.ParticipantService;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository repository;
    private final UserMapper userMapper;
    private final PoolMapper poolMapper;
    private final ParticipantMapper mapper;

    @Override
    public ParticipantDto get(PoolDto pool, UserDto user) {
        Participant userParticipant = pool.getParticipants().stream()
                .filter(participant -> participant.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(new Participant());

        return mapper.toDto(userParticipant);
    }

    @Override
    public int getUserStep(PoolDto poolDto, UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        Pool pool = poolMapper.toEntity(poolDto);

        Participant participant = repository.getParticipantByPoolAndUser(pool, user);

        if (participant == null) {
            return save(poolDto, userDto).getUserStep();
        }

        return participant.getUserStep();
    }

    @Override
    public ParticipantDto save(PoolDto poolDto, UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        Pool pool = poolMapper.toEntity(poolDto);
        Participant participant = repository.getParticipantByPoolAndUser(pool, user);

        if (participant == null) {
            participant = new Participant();
            participant.setUser(user);
            participant.setPool(pool);
            participant.setUserStep(0);

            participant = repository.saveAndFlush(participant);
            return mapper.toDto(participant);
        }

        return mapper.toDto(participant);
    }

    @Override
    public void save(ParticipantDto participantDto) {
        Participant participant = mapper.toEntity(participantDto);

        repository.saveAndFlush(participant);
    }

    @Override
    public void changeStep(PoolDto poolDto, UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        Pool pool = poolMapper.toEntity(poolDto);
        Participant participant = repository.getParticipantByPoolAndUser(pool, user);

        if (participant == null) {
            participant = new Participant();
            participant.setUser(user);
            participant.setPool(pool);
            participant.setUserStep(0);
        }

        if (participant.isFirstly()) {
            participant.setFirstly(false);
        }

        if (participant.getUserStep() + 1 < pool.getSteps().size()) {
            participant.setUserStep(participant.getUserStep() + 1);
        } else {
            participant.setUserStep(0);
            participant.setFirstly(true);
        }

        repository.save(participant);
    }

    @Override
    public void delete() {
        repository.deleteAll();
    }
}
