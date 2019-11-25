package com.hey.manager;

import com.hey.model.IWsMessage;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;

import java.util.HashSet;
import java.util.Set;

public class UserWsChannelManager {
    // key: user Id, value: ws topic id, each user listen on their own queue
    private static final String WS_TOPIC_ASYNC_MAP = "ws-topic-async-map";
    private EventBus eventBus;
    private SharedData sharedData;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setSharedData(SharedData sharedData) {
        this.sharedData = sharedData;
    }

    public void sendMessage(IWsMessage message, String userId) {
        getChannels(userId).setHandler(event -> {
            if (event.failed() || event.result() == null) return;

            for (String channelId : event.result()) {
                String jsonToStr = Json.encodePrettily(message);
                eventBus.send(channelId, jsonToStr);
            }
        });
    }

    public void selfSendMessage(IWsMessage message, String channelId) {
        String jsonToStr = Json.encodePrettily(message);
        eventBus.send(channelId, jsonToStr);
    }

    public Future<Boolean> registerChannel(String userId, String handlerId) {
        Promise<Boolean> promise = Promise.promise();
        sharedData.getAsyncMap(WS_TOPIC_ASYNC_MAP, event -> {
            AsyncMap<Object, Object> aMap = event.result();
            aMap.get(userId, event2 -> {
                HashSet<String> sets = event2.result() != null ? (HashSet<String>) event2.result() : new HashSet<>();
                boolean shouldNotify = sets.isEmpty();
                sets.add(handlerId);
                aMap.put(userId, sets, AsyncResult::mapEmpty);
                promise.complete(shouldNotify);
            });
        });
        return promise.future();
    }

    public Future<Boolean> removeChannel(String userId, String handlerId) {
        Promise<Boolean> promise = Promise.promise();
        sharedData.getAsyncMap(WS_TOPIC_ASYNC_MAP, event -> {
            AsyncMap<Object, Object> aMap = event.result();
            aMap.get(userId, event2 -> {
                boolean shouldNotify = false;
                if (event2.result() != null && !((Set) event2.result()).isEmpty()) {
                    HashSet<String> sets = (HashSet<String>) event2.result();
                    sets.remove(handlerId);
                    if (sets.isEmpty()) {
                        aMap.remove(userId, AsyncResult::mapEmpty);
                        shouldNotify = true;
                    }
                    aMap.put(userId, sets, AsyncResult::mapEmpty);
                } else {
                    aMap.remove(userId, AsyncResult::mapEmpty);
                    shouldNotify = true;
                }
                promise.complete(shouldNotify);
            });
        });
        return promise.future();
    }

    public Future<HashSet<String>> getChannels(String userId) {
        Promise<HashSet<String>> promise = Promise.promise();
        sharedData.getAsyncMap(WS_TOPIC_ASYNC_MAP, event -> {
            AsyncMap<Object, Object> aMap = event.result();
            aMap.get(userId, event2 -> promise.complete((HashSet<String>) event2.result()));
        });
        return promise.future();
    }
}
