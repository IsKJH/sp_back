package sp.spb.auth.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sp.spb.auth.user.entity.SocialLogin;
import sp.spb.auth.user.entity.SocialProvider;

import java.util.Optional;

@Repository
public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {
    Optional<SocialLogin> findByProviderAndProviderId(SocialProvider provider, String providerId);

    boolean existsByProviderAndProviderId(SocialProvider provider, String providerId);

}
