/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kuujo.copycat;

import net.jodah.concurrentunit.ConcurrentTestCase;
import net.jodah.concurrentunit.Waiter;
import net.kuujo.copycat.cluster.Cluster;
import net.kuujo.copycat.cluster.LocalClusterConfig;
import net.kuujo.copycat.cluster.Member;
import net.kuujo.copycat.log.InMemoryLog;
import net.kuujo.copycat.protocol.LocalProtocol;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Copycat test.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@Test
public abstract class AbstractCopycatTest extends ConcurrentTestCase {
  /**
   * Starts a cluster of contexts.
   */
  protected void startCluster(Set<Copycat> contexts) throws Throwable {
    Waiter waiter = new Waiter();
    for (Copycat context : contexts) {
      context.start().whenComplete((result, error) -> {
        waiter.assertNull(error);
        waiter.resume();
      });
    }

    waiter.await(10000, contexts.size());
  }

  /**
   * Starts a cluster of uniquely named CopyCat contexts.
   */
  protected Set<Copycat> startCluster(int numInstances) throws Throwable {
    Set<Copycat> contexts = createCluster(numInstances);
    startCluster(contexts);
    return contexts;
  }

  /**
   * Creates a cluster of uniquely named CopyCat contexts.
   */
  protected Set<Copycat> createCluster(int numInstances) {
    LocalProtocol protocol = new LocalProtocol();
    Set<Copycat> instances = new HashSet<>(numInstances);
    for (int i = 1; i <= numInstances; i++) {
      LocalClusterConfig config = new LocalClusterConfig();
      config.setLocalMember(String.valueOf(i));
      for (int j = 1; j <= numInstances; j++) {
        if (j != i) {
          config.addRemoteMember(String.valueOf(j));
        }
      }

      instances
          .add(Copycat
              .builder()
              .withStateMachine(new TestStateMachine())
              .withLog(new InMemoryLog())
              .withCluster(new Cluster<Member>(config)).withProtocol(protocol).build());
    }
    return instances;
  }

  protected static class TestStateMachine implements StateMachine {
    private final Map<String, Object> data = new HashMap<>();

    @Override
    public byte[] takeSnapshot() {
      return new byte[0];
    }

    @Override
    public void installSnapshot(byte[] snapshot) {

    }

    @Command
    public void set(String key, Object value) {
      data.put(key, value);
    }

    @Query
    public Object get(String key) {
      return data.get(key);
    }

    @Command
    public void delete(String key) {
      data.remove(key);
    }

    @Command
    public void clear() {
      data.clear();
    }
  }
}
