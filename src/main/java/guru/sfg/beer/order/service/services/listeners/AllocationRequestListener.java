package guru.sfg.beer.order.service.services.listeners;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.brewery.model.BeerOrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AllocationRequestListener {

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listener(Message message){
        BeerOrderDto beerOrderDto = (BeerOrderDto) message.getPayload();

    }
}
