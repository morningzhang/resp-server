/*
 * Copyright (c) 2015-2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.resp.command;

import java.util.Optional;

import com.github.tonivade.resp.protocol.RedisToken;

public interface Session {
  String getId();
  void publish(RedisToken msg);
  void close();
  void destroy();
  <T> Optional<T> getValue(String key);
  void putValue(String key, Object value);
  <T> Optional<T> removeValue(String key);
}
