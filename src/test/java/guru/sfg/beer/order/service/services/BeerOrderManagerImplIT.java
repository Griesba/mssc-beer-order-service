package guru.sfg.beer.order.service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.repositories.CustomerRepository;
import guru.sfg.beer.order.service.services.beer.BeerServiceImpl;
import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerOrderPagedList;
import guru.sfg.brewery.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.*;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(WireMockExtension.class)
@SpringBootTest
class BeerOrderManagerImplIT {

    @Autowired
    BeerOrderManager beerOrderManager;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderService beerOrderService;

    @Autowired
    WireMockServer wireMockServer;

    Customer testCustomer;

    @Autowired
    ObjectMapper objectMapper;

    UUID beerId = UUID.randomUUID();

    @TestConfiguration
    static class RestTemplateBuilderProvider {

        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer() {
            WireMockServer server = with(wireMockConfig().port(8083));
            server.start();
            return server;
        }
    }

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.save(Customer.builder().customerName("Test customer").build());
    }

    @Test
    public void testNewToAllocated() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("1234").build();
        //BeerPagedList list = new BeerPagedList(Arrays.asList(beerDto));

        wireMockServer.stubFor(WireMock.get(BeerServiceImpl.BEER_UPC_SERVICE_PATH + "12345").willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
        BeerOrder beerOrder = createBeerOrder();

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            //todo - ALLOCATED_STATUS
            assertEquals(BeerOrderStatusEnum.ALLOCATION_PENDING, foundOrder.getOrderStatus());
        });

        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            BeerOrderLine beerOrderLine = foundOrder.getBeerOrderLines().iterator().next();

            assertEquals(beerOrderLine.getOrderQuantity(), beerOrderLine.getQuantityAllocated());
        });

        BeerOrder savedBeerOrder2 = beerOrderManager.newBeerOrder(beerOrder);

        assertNotNull(savedBeerOrder2);

        assertEquals(BeerOrderStatusEnum.ALLOCATED, savedBeerOrder2.getOrderStatus());

        savedBeerOrder2.getBeerOrderLines().forEach(beerOrderLine -> {
            assertEquals(beerOrderLine.getOrderQuantity(), beerOrderLine.getQuantityAllocated());
        });
    }

    private BeerOrder createBeerOrder() {
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(testCustomer)
                .build();

        Set<BeerOrderLine> lines = new HashSet<>();
        lines.add(BeerOrderLine.builder()
                .beerId(beerId)
                .upc("12345")
                .beerOrder(beerOrder)
                .orderQuantity(1)
                .build());
        beerOrder.setBeerOrderLines(lines);
        return beerOrder;
    }

    @Test
    void newBeerOrder() {
    }

    @Test
    void processValidationResult() {
    }

    @Test
    void beerOrderAllocationSucceeded() {
    }

    @Test
    void beerOrderAllocationPendingInventory() {
    }

    @Test
    void beerOderAllocationFailed() {
    }
}