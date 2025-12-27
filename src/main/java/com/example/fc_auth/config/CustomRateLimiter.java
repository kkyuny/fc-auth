package com.example.fc_auth.config;


import com.example.fc_auth.repository.AppRoleRepository;
import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.local.LocalBucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomRateLimiter {
    private final Map<String, Bucket> keyToBucketMap = new ConcurrentHashMap<>();
    private final Duration REFILL_PERIOD_ONE_MINUTE = Duration.ofMinutes(1L);

    private final AppRoleRepository appRoleRepository;

    public boolean tryConsume(Long appId, Long apiId){
        String key = appId.toString() + ":" + apiId.toString();
        Bucket bucket = keyToBucketMap.computeIfAbsent(key, k-> createBucket(appId, apiId));

        log.info(String.format("throttling : %s count ++1", key));
        return bucket.tryConsume(1);
    }

    public LocalBucket createBucket(Long appId, Long apiId){
        Integer threshold = appRoleRepository.findByAppIdAndApiId(appId, apiId).getThreshold();
        return Bucket.builder()
                .addLimit(BandwidthBuilder.builder().capacity(threshold).refillIntervally(threshold, REFILL_PERIOD_ONE_MINUTE).build())
                .build();
    }
}