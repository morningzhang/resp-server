/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.resp.command.server;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.resp.command.CommandRule;
import com.github.tonivade.resp.command.CommandUnderTest;
import com.github.tonivade.resp.protocol.RedisToken;

@CommandUnderTest(TimeCommand.class)
public class TimeCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<RedisToken>> captor;

    @Test
    public void testExecute() {
        rule.execute().verify().addArray(captor.capture());

        Collection<RedisToken> value = captor.getValue();

        Iterator<RedisToken> iterator = value.iterator();
        RedisToken secs = iterator.next();
        RedisToken mics = iterator.next();

        System.out.println(secs);
        System.out.println(mics);
    }

}