/*
 * Copyright (c) 2015-2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.resp.protocol.test;

import static com.github.tonivade.equalizer.Equalizer.comparing;
import static com.github.tonivade.equalizer.Equalizer.equalizer;

import java.util.Objects;

public class Data {
  private final int id;
  private final String value;

  public Data(int id, String value) {
    this.id = id;
    this.value = value;
  }

  public int getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, value);
  }

  @Override
  public boolean equals(Object obj) {
    return equalizer(this)
        .append(comparing(Data::getId))
        .append(comparing(Data::getValue))
        .applyTo(obj);
  }

  @Override
  public String toString() {
    return "Data [id=" + id + ", value=" + value + "]";
  }
}
