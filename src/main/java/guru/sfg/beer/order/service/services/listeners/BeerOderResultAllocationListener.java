package guru.sfg.beer.order.service.services.listeners;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.services.BeerOrderManager;
import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.events.AllocationOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOderResultAllocationListener {
    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocationOrderResponse allocationOrderResponse){
        BeerOrderDto beerOrderDto = allocationOrderResponse.getBeerOrderDto();
        if (!allocationOrderResponse.getAllocationError() && !allocationOrderResponse.getPendingInventory()) {
            beerOrderManager.beerOrderAllocationSucceeded(beerOrderDto);
        } else if (!allocationOrderResponse.getAllocationError() && allocationOrderResponse.getPendingInventory()) {
            beerOrderManager.beerOrderAllocationPendingInventory(beerOrderDto);
        } else if (allocationOrderResponse.getAllocationError()) {
            beerOrderManager.beerOderAllocationFailed(beerOrderDto);
        }

    }
}
