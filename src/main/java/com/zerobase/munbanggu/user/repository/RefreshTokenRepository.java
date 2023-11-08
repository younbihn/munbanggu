package com.zerobase.munbanggu.user.repository;

import com.zerobase.munbanggu.user.model.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findById(Long id);
}
