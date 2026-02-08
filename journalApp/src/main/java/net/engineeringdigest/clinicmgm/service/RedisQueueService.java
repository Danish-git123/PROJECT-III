package net.engineeringdigest.clinicmgm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisQueueService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private String queueKey(Long doctorId){
        return  "queue:doctor: "+doctorId;
    }

//    Add token to queue (FIFO via score)
    public void addToQueue(Long doctorId,Long tokenId,long score){
        redisTemplate.opsForZSet().add(queueKey(doctorId),tokenId.toString(),score);
    }

//    Remove token from QUeue
    public void removeFormQueue(Long docotrId,Long tokenId){
        redisTemplate.opsForZSet().remove(queueKey(docotrId),tokenId.toString());
    }

    public List<Long> getQueue(Long doctorId){
        Set<String> values=redisTemplate.opsForZSet().range(queueKey(doctorId),0,-1);

        if(values == null) return Collections.emptyList();

        return values.stream().map(Long :: valueOf).collect(Collectors.toList());
    }

    public void clearQueue(Long doctorId){
        redisTemplate.delete(queueKey(doctorId));
    }

    public Long popNext(Long doctorId){
        String key=queueKey(doctorId);

        Set<String> values = redisTemplate.opsForZSet().range(key, 0, 0);

        if(values==null || values.isEmpty()){
            return  null;
        }

        String tokenId=values.iterator().next();

        redisTemplate.opsForZSet().remove(key,tokenId);

        return Long.valueOf(tokenId);

    }

}
