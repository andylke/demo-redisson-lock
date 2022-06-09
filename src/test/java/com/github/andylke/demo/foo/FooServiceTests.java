package com.github.andylke.demo.foo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

@SpringBootTest
class FooServiceTests {

  @TestConfiguration(proxyBeanMethods = false)
  static class FooServiceTestConfiguration {}

  @Autowired private FooRepository fooRepository;

  @Autowired private FooService fooService;

  @BeforeEach
  void beforeEach() {}

  @Test
  void saveOne() {
    final Foo result = fooService.save(0, "zero");
    System.out.println(
        "Saved " + ReflectionToStringBuilder.toString(result, ToStringStyle.NO_CLASS_NAME_STYLE));

    assertThat(fooRepository.findById(0).orElseThrow().getText()).isEqualTo("zero");
  }

  @Test
  void saveFiveInParallel_givenUniqueIds() throws InterruptedException, ExecutionException {
    final Map<Integer, String> records =
        Map.of(1, "one", 2, "two", 3, "three", 4, "four", 5, "five");

    final List<CompletableFuture<?>> futures = new ArrayList<>();

    final CountDownLatch countDownLatch = new CountDownLatch(records.size());
    for (Entry<Integer, String> record : records.entrySet()) {
      futures.add(
          CompletableFuture.runAsync(
              () -> {
                countDownLatch.countDown();

                try {
                  countDownLatch.await();
                } catch (InterruptedException e) {
                }

                final Foo result = fooService.save(record.getKey(), record.getValue());
                System.out.println(
                    "Saved "
                        + ReflectionToStringBuilder.toString(
                            result, ToStringStyle.NO_CLASS_NAME_STYLE));

                assertThat(fooRepository.findById(record.getKey()).orElseThrow().getText())
                    .isEqualTo(record.getValue());
              }));
    }

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
  }

  @Test
  void saveFiveInParallel_givenSameId() throws InterruptedException, ExecutionException {
    final Map<Integer, String> records =
        Map.of(1, "one", 2, "two", 3, "three", 4, "four", 5, "five");

    final List<CompletableFuture<?>> futures = new ArrayList<>();

    final CountDownLatch countDownLatch = new CountDownLatch(records.size());
    for (Entry<Integer, String> record : records.entrySet()) {
      futures.add(
          CompletableFuture.runAsync(
              () -> {
                countDownLatch.countDown();

                try {
                  countDownLatch.await();
                } catch (InterruptedException e) {
                }

                final Foo result = fooService.save(6, record.getValue());
                System.out.println(
                    "Saved "
                        + ReflectionToStringBuilder.toString(
                            result, ToStringStyle.NO_CLASS_NAME_STYLE));
              }));
    }

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

    assertThat(fooRepository.findById(6).orElseThrow().getText())
        .contains("one", "two", "three", "four", "five");
  }
}
