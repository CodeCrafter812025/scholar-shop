package net.holosen.app.config;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CacheEvictScheduler {
    @CacheEvict("apiCache30m")
    @Scheduled(fixedDelay = 18000)
    public void evictApiCache30M(){
        System.out.println(LocalDateTime.now().toLocalDate() + " - " +
                LocalDateTime.now().toLocalDate() + " > API 30 Min Cache Evict");
    }

    @CacheEvict("apiCache15m")
    @Scheduled(fixedDelay = 54000)
    public void evictApiCache15M(){
        System.out.println(LocalDateTime.now().toLocalDate() + " - " +
                LocalDateTime.now().toLocalDate() + " > API 15 Min Cache Evict");
    }
}
