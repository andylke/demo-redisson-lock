package com.github.andylke.demo.foo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FooRepository extends JpaRepository<Foo, Integer> {

  Optional<Foo> findById(Integer id);
}
