package com.rehund.healthcare.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration LOCK_TTL = Duration.ofSeconds(10);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 100;

    public <T> Optional<T> get(String key, Class<T> type) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(objectMapper.readValue(value, type));
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            log.error("Error deserializing cache for key: {}", key, exception);
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<T> get(String key, TypeReference<T> typeReference) {
        String value = redisTemplate.opsForValue().get(key);
        try{
            return Optional.ofNullable(objectMapper.readValue(value, typeReference));
        } catch (JsonProcessingException | IllegalArgumentException exception){
            log.error("Error while deserializing cache value for key: {}: {}", key, exception.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public <T> void put(String key, T value) {
        try{
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue);
        } catch (JsonProcessingException exception){
            log.error("Error while serializing cache value for key: {}: {}", key, exception.getMessage());
        }
    }

    @Override
    public <T> void put(String key, T value, Duration ttl) {
        try{
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, ttl);
        } catch (JsonProcessingException exception){
            log.error("Error while serializing cache value for key: {}: {}", key, exception.getMessage());
        }
    }

    @Override
    public void evict(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public <T> T getOrLoad(String key, Class<T> type, Duration ttl, Supplier<T> loader) {

        Optional<T> cachedValue = get(key, type);

        if (cachedValue.isPresent()) {
            return cachedValue.get();
        }

        // if cache miss
        String lockKey = key + ":lock";
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", LOCK_TTL);

        try {
            if (Boolean.TRUE.equals(acquired)){

                // lock acquired, load from source
                cachedValue = get(key, type);
                if (cachedValue.isPresent()) {
                    return cachedValue.get();
                }

                // Load from source
                log.debug("Cache miss for key: {}. Loading from source.", key);
                T value = loader.get();

                // Store in cache
                put(key, value, ttl);
                return value;

            } else {
                // Lock not acquired, wait and retry
                return waitAndRetry(key, type, loader, 0);
            }
        } finally {
            // Release Lock only when acquired
            if (Boolean.TRUE.equals(acquired)){
                redisTemplate.delete(lockKey);
            }
        }
    }

    private <T> T waitAndRetry(
            String key,
            Class<T> type,
            Supplier<T> loader,
            int attempt
    )
    {
        if (attempt >= MAX_RETRY_ATTEMPTS){
            log.warn("Max retry attempts reached for key: {}. Loading from source directly.", key);
            return loader.get();
        }

        try {
            Thread.sleep(RETRY_DELAY_MS);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();

            log.error("Interrupted while waiting for cache to populate: {}", key, e);
            return loader.get();
        }

        // check cache again
        Optional<T> cached = get(key, type);
        return cached.orElseGet(() -> waitAndRetry(key, type, loader, attempt + 1));
    }

}
