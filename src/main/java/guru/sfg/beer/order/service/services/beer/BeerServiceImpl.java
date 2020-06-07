package guru.sfg.beer.order.service.services.beer;

import guru.sfg.brewery.model.BeerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = false)
@Service
public class BeerServiceImpl implements BeerService {
    public static final String BEER_UPC_SERVICE_PATH = "/api/v1/beerUpc/";
    public final static String BEER_ID_SERVICE_PATH = "/api/v1/beer/";
    private String beerServiceHost;

    private final RestTemplate restTemplate;

    public BeerServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void setBeerServiceHost(String host) {
        this.beerServiceHost = host;
    }

    public Optional<BeerDto> getBeerById(UUID uuid) {
        String uri = beerServiceHost + BEER_ID_SERVICE_PATH + uuid.toString();
        return Optional.of(restTemplate.getForObject(uri, BeerDto.class));
    }

    public Optional<BeerDto> getBeerByUpc(String upc) {
        String uri = beerServiceHost + BEER_UPC_SERVICE_PATH + upc;
        log.debug("Calling Beer Service: " + uri);
        return Optional.of(restTemplate.getForObject(uri, BeerDto.class));
    }

    public BeerDto getBeerDto(UUID beerId) {

        log.debug("Calling Beer Service");

        String uri = beerServiceHost + BEER_ID_SERVICE_PATH + beerId;
        ResponseEntity<BeerDto> responseEntity = restTemplate.exchange(uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BeerDto>() {
                },
                (Object) beerId);

        return Objects.requireNonNull(responseEntity.getBody());
    }
}
