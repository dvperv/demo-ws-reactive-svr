package org.dp.demowsreactivesvr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dp.demowsreactivesvr.model.OutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static java.time.LocalTime.now;
import static java.util.UUID.randomUUID;

@Component
public class WsSenderHandler implements WebSocketHandler {
    private static final ObjectMapper json = new ObjectMapper();

    private Flux<String> eventFlux = Flux.generate(sink -> {
        OutputMessage outputMessage = new OutputMessage(
                randomUUID().toString(),
                "Hi, there! I'm tester!",
                now().toString()
        );

        try {
            sink.next(json.writeValueAsString(outputMessage));
        } catch (JsonProcessingException e) {
            sink.error(e);
        }
    });

    private Flux<String> intervalFlux = Flux.interval(Duration.ofMillis(1000L))
            .zipWith(eventFlux, (time, event) -> event);
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(intervalFlux
                .map(session::textMessage))
                .and(
                    session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .log()
                );
    }
}

