package com.rehund.healthcare.service.cache;

import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {

    <T> Optional<T> get(String key, Class<T> type);
    <T> Optional<T> get(String key, TypeReference<T> typeReference);
    <T> void put(String key, T value);
    <T> void put(String key, T value, Duration ttl);
    void evict(String key);
}
