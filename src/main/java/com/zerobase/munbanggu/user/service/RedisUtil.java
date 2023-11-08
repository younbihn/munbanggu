package com.zerobase.munbanggu.user.service;


import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisUtil {
  private final StringRedisTemplate stringRedisTemplate;

  public String getData(String key){
    ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
    return valueOperations.get(key);
  }

  public void setData(String key, String value, long duration) {
    ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
    valueOperations.set(key,value,duration, TimeUnit.MINUTES);
  }
  public void deleteData(String key){
    stringRedisTemplate.delete(key);
  }
}
