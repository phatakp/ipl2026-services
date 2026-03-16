package com.phatakp.ipl_services.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phatakp.ipl_services.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ClerkJwksProvider {

    private final AppProperties appProperties;
    private final Map<String, PublicKey> keyCache = new HashMap<>();
    private long lastFetchTime = 0;
    private static final long CACHE_TTL = 3600000; //1 hour

    public PublicKey getPublicKey(String keyId) throws Exception {
        if (keyCache.containsKey(keyId) && System.currentTimeMillis() - lastFetchTime < CACHE_TTL) {
            return keyCache.get(keyId);
        }
        refreshKeys();
        return keyCache.get(keyId);
    }

    private void refreshKeys() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jwks = mapper.readTree(new URL(appProperties.getClerkJwksUrl()));

        JsonNode keys = jwks.get("keys");
        for (JsonNode key : keys) {
            String id = key.get("kid").asText();
            String kty = key.get("kty").asText();
            String algorithm = key.get("alg").asText();

            if ("RSA".equals(kty) && "RS256".equals(algorithm)) {
                String n = key.get("n").asText();
                String e = key.get("e").asText();

                PublicKey publicKey = createPublicKey(n,e);
                keyCache.put(id,publicKey);
            }
        }
        lastFetchTime = System.currentTimeMillis();
    }

    private PublicKey createPublicKey(String modulus, String exponent) throws Exception {
        byte[] modulusBytes = Base64.getUrlDecoder().decode(modulus);
        byte[] exponentBytes = Base64.getUrlDecoder().decode(exponent);

        BigInteger modulusBigInteger = new BigInteger(1,modulusBytes);
        BigInteger exponentBigInteger = new BigInteger(1,exponentBytes);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulusBigInteger, exponentBigInteger);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);

    }
}
