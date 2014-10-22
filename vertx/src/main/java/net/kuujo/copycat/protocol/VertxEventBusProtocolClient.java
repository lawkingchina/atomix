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
package net.kuujo.copycat.protocol;

import net.kuujo.copycat.internal.util.Assert;
import net.kuujo.copycat.spi.protocol.ProtocolClient;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;

import java.util.concurrent.CompletableFuture;

/**
 * Vert.x event bus protocol client.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class VertxEventBusProtocolClient implements ProtocolClient {
  private final ProtocolReader reader = new ProtocolReader();
  private final ProtocolWriter writer = new ProtocolWriter();
  private final String address;
  private Vertx vertx;

  public VertxEventBusProtocolClient(String address, Vertx vertx) {
    this.address = Assert.isNotNull(address, "Vert.x event bus address cannot be null");
    this.vertx = Assert.isNotNull(vertx, "Vert.x instance cannot be null");
  }

  @Override
  public CompletableFuture<PingResponse> ping(final PingRequest request) {
    final CompletableFuture<PingResponse> future = new CompletableFuture<>();
    vertx.eventBus().sendWithTimeout(address, writer.writeRequest(request), 5000, new Handler<AsyncResult<Message<byte[]>>>() {
      @Override
      public void handle(AsyncResult<Message<byte[]>> result) {
        if (result.failed()) {
          future.completeExceptionally(result.cause());
        } else {
          future.complete(reader.readResponse(result.result().body()));
        }
      }
    });
    return future;
  }

  @Override
  public CompletableFuture<SyncResponse> sync(final SyncRequest request) {
    final CompletableFuture<SyncResponse> future = new CompletableFuture<>();
    vertx.eventBus().sendWithTimeout(address, writer.writeRequest(request), 5000, new Handler<AsyncResult<Message<byte[]>>>() {
      @Override
      public void handle(AsyncResult<Message<byte[]>> result) {
        if (result.failed()) {
          future.completeExceptionally(result.cause());
        } else {
          future.complete(reader.readResponse(result.result().body()));
        }
      }
    });
    return future;
  }

  @Override
  public CompletableFuture<PollResponse> poll(final PollRequest request) {
    final CompletableFuture<PollResponse> future = new CompletableFuture<>();
    vertx.eventBus().sendWithTimeout(address, writer.writeRequest(request), 5000, new Handler<AsyncResult<Message<byte[]>>>() {
      @Override
      public void handle(AsyncResult<Message<byte[]>> result) {
        if (result.failed()) {
          future.completeExceptionally(result.cause());
        } else {
          future.complete(reader.readResponse(result.result().body()));
        }
      }
    });
    return future;
  }

  @Override
  public CompletableFuture<SubmitResponse> submit(final SubmitRequest request) {
    final CompletableFuture<SubmitResponse> future = new CompletableFuture<>();
    vertx.eventBus().sendWithTimeout(address, writer.writeRequest(request), 5000, new Handler<AsyncResult<Message<byte[]>>>() {
      @Override
      public void handle(AsyncResult<Message<byte[]>> result) {
        if (result.failed()) {
          future.completeExceptionally(result.cause());
        } else {
          future.complete(reader.readResponse(result.result().body()));
        }
      }
    });
    return future;
  }

  @Override
  public CompletableFuture<Void> connect() {
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<Void> close() {
    return CompletableFuture.completedFuture(null);
  }

}
