package ru.dmsmirnov.rent.infrastructure.store.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.dmsmirnov.rent.infrastructure.store.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

}
