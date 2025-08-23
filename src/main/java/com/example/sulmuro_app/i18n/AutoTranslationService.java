package com.example.sulmuro_app.i18n;

import com.example.sulmuro_app.i18n.mt.MTClient;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AutoTranslationService implements TranslationService {

    private final MTClient mtClient;

    private final Cache<String,String> cache =
            Caffeine.newBuilder()
                    .expireAfterWrite(Duration.ofMinutes(60))
                    .maximumSize(50_000)
                    .build();

    @Value("${app.i18n.cacheTtlMinutes:60}") private int ttl;

    @Override
    public String translate(String originalText, Language target, String sourceOrNull) {
        if (originalText == null || originalText.isBlank()) return originalText;
        String ck = target.deeplCode() + "|" + originalText;
        String hit = cache.getIfPresent(ck);
        if (hit != null) return hit;

        String out = mtClient.translate(originalText, sourceOrNull, target.deeplCode());
        if (out == null || out.isBlank()) out = originalText;
        cache.put(ck, out);
        return out;
    }
}
