package org.dp.demowsreactivesvr;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

public class WsReceiverHandler implements WebSocketHandler {
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive()
                .doOnNext(message -> {
                    System.out.println(message.getPayloadAsText());
                })
//                .concatMap(message -> {
//                    // ...
//                })
                .then();
    }
}

