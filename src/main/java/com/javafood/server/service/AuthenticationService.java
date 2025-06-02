package com.javafood.server.service;

import com.javafood.server.dto.request.AuthenticationRequest;
import com.javafood.server.dto.request.IntrospectRequest;
import com.javafood.server.dto.request.LogoutRequest;
import com.javafood.server.dto.response.AuthenticationResponse;
import com.javafood.server.dto.response.IntrospectResponse;
import com.javafood.server.entity.InvalidatedToken;
import com.javafood.server.entity.UserEntity;
import com.javafood.server.exception.AppException;
import com.javafood.server.exception.ErrorCode;
import com.javafood.server.repository.InvalidatedTokenRepository;
import com.javafood.server.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthenticationService {
    @Autowired
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.sign_key}")
    protected String signerKey;

    UserRepository userRepository;

    // login
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws JOSEException {
        var user = userRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow( ()-> new AppException(ErrorCode.USER_NOT_EXISTS));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if(!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var token = generateToken(user);
        return AuthenticationResponse.builder().id(user.getId()).role(user.getRole()).username(user.getUsername()).token(token).isAuthenticated(true).build();
    }

    // logout
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());
        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expTime = signToken.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expTime(expTime).build();
        invalidatedTokenRepository.save(invalidatedToken);
    }

    // Tao token
    private String generateToken(UserEntity userEntity) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512); // Thuật toán sử dụng

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder() // Body token gửi đi
                .subject(userEntity.getUsername())
                .issuer("hoangtuan") // Chủ sở hữu
                .issueTime(new Date()) // Thời gian bắt đầu token
                .expirationTime(new Date(
                        Instant.now().plus(3, ChronoUnit.HOURS).toEpochMilli()
                )) // thời hạn token sau 1h
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", userEntity.getRole()) // Custome them gia tri vao token
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject()); // Tạo payload
        JWSObject jwsObject = new JWSObject(header, payload);

       try {
           jwsObject.sign(new MACSigner(signerKey));
           return jwsObject.serialize();
       } catch (RuntimeException e) {
           log.info("Không thẻ tạo token");
           throw new RuntimeException(e);
       }

    }

    // Xác minh token còn hiệu lực hay không
    public IntrospectResponse introspectToken(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        try {
            verifyToken(token);
        } catch (AppException e){
            return IntrospectResponse.builder().valid(false).build();
        }
        return IntrospectResponse.builder().valid(true).build();
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {

        JWSVerifier jwsVerifier = new MACVerifier(signerKey.getBytes()); // Xac định signer key
        SignedJWT signedJWT = SignedJWT.parse(token); // Parse token
        Date expTimeToken = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(jwsVerifier); // Verify token

        if(!verified && expTimeToken.after(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }
}
