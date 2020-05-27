package guru.sfg.beer.order.service.sm;

import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.services.BeerOrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static guru.sfg.beer.order.service.services.BeerOrderManagerImpl.ORDER_ID_HEADER;

@RequiredArgsConstructor
@Configuration
@EnableStateMachineFactory
public class BeerStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    //@Qualifier("validateOrderAction") no need the attribute has the same name
    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> validateOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> states) throws Exception {
        states.withStates()
                .initial(BeerOrderStatusEnum.NEW)
                .states(EnumSet.allOf(BeerOrderStatusEnum.class))
                .end(BeerOrderStatusEnum.DELIVERED)
                .end(BeerOrderStatusEnum.PICKED_UP)
                .end(BeerOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(BeerOrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> transitions) throws Exception {
        transitions.withExternal()
                .source(BeerOrderStatusEnum.NEW)
                .target(BeerOrderStatusEnum.VALIDATION_PENDING)
                .event(BeerOrderEventEnum.VALIDATE_ORDER)
                .action(validateOrderAction)
                .and().withExternal()
                    .source(BeerOrderStatusEnum.NEW)
                    .target(BeerOrderStatusEnum.VALIDATE)
                    .event(BeerOrderEventEnum.VALIDATION_PASSED)
                .and().withExternal()
                    .source(BeerOrderStatusEnum.NEW)
                    .target(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
                    .event(BeerOrderEventEnum.VALIDATION_FAILED);
    }
}
