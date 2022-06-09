package com.github.andylke.demo.foo;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FooService {

  @Autowired private FooRepository fooRepository;

  @Autowired private RedissonClient redissonClient;

  @Transactional
  public Foo save(int id, String text) {
    RLock lock = redissonClient.getLock("foo:" + id);
    boolean aquiredLock;
    try {
      aquiredLock = lock.tryLock(10, 5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new IllegalStateException("Failed to aquired lock for " + id, e);
    }
    if (aquiredLock == false) {
      throw new IllegalStateException("Failed to aquired lock for " + id);
    }

    try {

      Foo foo = fooRepository.findById(id).orElseThrow();
      if (StringUtils.isBlank(foo.getText())) {
        foo.setText(text);
      } else {
        foo.setText(foo.getText() + " " + text);
      }
      return fooRepository.saveAndFlush(foo);

    } finally {
      lock.unlock();
    }
  }
}
