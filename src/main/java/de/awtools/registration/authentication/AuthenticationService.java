package de.awtools.registration.authentication;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

import de.awtools.registration.time.TimeService;
import de.awtools.registration.user.PrivilegeEntity;
import de.awtools.registration.user.PrivilegeRepository;
import de.awtools.registration.user.UserAccountEntity;
import de.awtools.registration.user.UserAccountRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    /** Should this be configured? */
    private static final long EXPIRATION_DAYS = 3;

    /** Could the be a better issuer? */
    private static final String ISSUER = "awregister";

    /** Should this be configured? */
    private static final ZoneId DEFAULT_TIME_ZONE_ID = ZoneId.systemDefault();

    private static final KeyPair KEY_PAIR;

    static {
        // TODO Auslagerung in eine Datei?!
        KEY_PAIR = Keys.keyPairFor(SignatureAlgorithm.RS256);
    }

    private final AuthenticationRepository authenticationRepository;
    private final UserAccountRepository userAccountRepository;
    private final PrivilegeRepository privilegeRepository;
    private final TimeService timeService;

    @Autowired
    public AuthenticationService(AuthenticationRepository authenticationRepository, UserAccountRepository userAccountRepository,
                PrivilegeRepository privilegeRepository, TimeService timeService) {

        this.authenticationRepository = authenticationRepository;
        this.userAccountRepository = userAccountRepository;
        this.privilegeRepository = privilegeRepository;
        this.timeService = timeService;
    }

    public Token login(String nickname, String password) {
        UserAccountEntity user = userAccountRepository.findByNickname(nickname)
                .orElseThrow(() -> new EntityNotFoundException("Unknown user with nickname=[" + nickname + "]."));

        if (!user.getPassword().equals(password)) {
            // TODO Mit irgendwas signalisieren, dass der Login-Versuch nicht efolgreich war.
            return null;
        }

        return token(loadUserByUsername(nickname));
    }

    public void logout(Token token) {
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final UserAccountEntity user = userAccountRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user with nickname=[" + username + "]."));

        AWUserDetails.AWUserDetailsBuilder userDetailsBuilder = AWUserDetails.AWUserDetailsBuilder
                .of(user.getNickname(), user.getPassword().get());

        Set<PrivilegeEntity> privileges = privilegeRepository.findByNickname(user.getNickname());

        for (PrivilegeEntity privilege : privileges) {
            userDetailsBuilder.addGrantedAuthority(new SimpleGrantedAuthority(privilege.getName()));
        }

        return userDetailsBuilder.build();
    }

    Token token(UserDetails user) {
        LocalDateTime tokenExpiration = timeService.now().plusDays(EXPIRATION_DAYS);

        String jws = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(ISSUER)
                .setIssuedAt(timeService.currently())
                .setExpiration(TimeService.convertToDateViaInstant(DEFAULT_TIME_ZONE_ID, tokenExpiration))
                .signWith(KEY_PAIR.getPrivate())
                .compact();

        return new Token(jws);
    }

}
