package dev.nils.notes_modulith;

import dev.nils.notes_modulith.notes.domain.model.NoteCreatedEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TestRabbitListener {

    @Getter
    private volatile NoteCreatedEvent receivedEvent;

    private volatile CountDownLatch latch = new CountDownLatch(1);

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void receiveMessage(NoteCreatedEvent event) {
        log.info("Test listener received event: {}", event);
        this.receivedEvent = event;
        latch.countDown();
    }

    public boolean awaitMessage(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    public void reset() {
        this.receivedEvent = null;
        this.latch = new CountDownLatch(1);
    }
}
