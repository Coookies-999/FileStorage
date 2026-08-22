package com.FileStorage.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret}") String secret){

        //this code basicaaly generates key and use that
//        try{
//            SecretKey key= KeyGenerator.getInstance("HmacSHA256").generateKey();
//            secretKey= Base64.getEncoder().encodeToString(key.getEncoded());
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );

    }
    public String generateToken(String userId){
            return Jwts.builder()
                    .subject(userId)
                    .issuedAt(new Date())
                    .expiration(
                            new Date(System.currentTimeMillis()+ 1000*60*60)
                    )
                    .signWith(secretKey)
                    .compact();
    }

    public String extractUserId(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
