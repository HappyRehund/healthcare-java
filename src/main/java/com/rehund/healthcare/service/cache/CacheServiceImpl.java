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

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        String value = redisTemplate.opsForValue().get(key);
        try{
            return Optional.ofNullable(objectMapper.readValue(value, type));
        } catch (JsonProcessingException | IllegalArgumentException exception){
            log.error("Error while deserializing cache value for key: {}: {}", key, exception.getMessage());
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
            throw new RuntimeException(exception);
        }
    }

    @Override
    public <T> void put(String key, T value, Duration ttl) {
        try{
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, ttl);
        } catch (JsonProcessingException exception){
            log.error("Error while serializing cache value for key: {}: {}", key, exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void evict(String key) {
        redisTemplate.delete(key);
    }
}
