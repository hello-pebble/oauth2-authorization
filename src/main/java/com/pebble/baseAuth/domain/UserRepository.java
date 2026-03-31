package com.pebble.baseAuth.domain;

import java.util.Optional;

public interface UserRepository {

    boolean existsByUsernameAndDeletedAtIsNull(String username);

    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

    Optional<User> findByProviderAndProviderIdAndDeletedAtIsNull(String provider, String providerId);

    User save(User user);

}
