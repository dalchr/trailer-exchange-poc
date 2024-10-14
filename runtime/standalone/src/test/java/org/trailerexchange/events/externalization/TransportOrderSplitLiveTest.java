package org.trailerexchange.events.externalization;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;
import org.trailerexchange.TrailerExchangeApplication;
import org.trailerexchange.carriers.domain.model.TransportLeg;
import org.trailerexchange.carriers.domain.model.TransportLegRepository;
import org.trailerexchange.carriers.domain.model.TransportLegStatus;
import org.trailerexchange.data.PredefinedDataFixtures;
import org.trailerexchange.events.externalization.listener.TestKafkaListenerConfig;
import org.trailerexchange.events.externalization.listener.TestListener;
import org.trailerexchange.transportorders.TransportOrderController;
import org.trailerexchange.transportorders.domain.model.Buyer;
import org.trailerexchange.transportorders.domain.model.TransportOrder;
import org.trailerexchange.transportorders.domain.model.TransportOrderRepository;
import org.trailerexchange.transportorders.domain.model.TransportStatus;
import org.trailerexchange.transportorders.dto.TransportOrderRequestDTO;

@Testcontainers
@SpringBootTest(classes = { TrailerExchangeApplication.class, TestKafkaListenerConfig.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
class TransportOrderSplitLiveTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestListener listener;

    @Autowired
    private TransportOrderController transportOrderController;

    @Autowired
    private TransportOrderRepository transportOrders;

    @Autowired
    private TransportLegRepository transportLegs;

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Container
    public static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer()
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
    }

    static {
        Awaitility.setDefaultTimeout(ofSeconds(50));
        Awaitility.setDefaultPollDelay(ofMillis(100));
    }

    @BeforeEach
    void beforeEach() {
        listener.reset();
        transportOrders.deleteAll();
        transportLegs.deleteAll();
    }

    @Test
    void whenHandleTransportOrder_thenSplitIntoTwoLegs_thenAssignAndWaitForEachToFinish() {
        Buyer buyer = PredefinedDataFixtures.johnD();

        TransportOrderRequestDTO handleOrderRequest = new TransportOrderRequestDTO();
        handleOrderRequest.setBuyerId(buyer.getId());
        handleOrderRequest.setStartLocation(PredefinedDataFixtures.goteborg());
        handleOrderRequest.setEndLocation(PredefinedDataFixtures.oslo());

        // handleTransportOrder, requests processing of the transport order, which does as follows (in the background):
        // ... 1. stores request transport order
        // ... 2. splits start and end locations into two legs with exchange location between
        // ... 3. assigns first leg to first carrier (carrier emulates of the actual transport in the background)
        // ... 4. assigns second leg to second carrier (carrier emulates of the actual transport in the background)

        TransportOrder transportOrder = transportOrderController.handleTransportOrder(handleOrderRequest).getBody();

        // ... 5. awaits the transport completion (7 events should execute in the background)
        await().untilAsserted(() ->
            assertThat(listener.getEvents())
                .hasSize(7));

        List<TransportLeg> transportLegs = this.transportLegs.findByOrder_Id(transportOrder.getId());
        assertThat(transportLegs).hasSize(2);

        // ... 6. verifies the final status of the two legs and the complete transport order

        TransportLeg firstTransportLeg = transportLegs.get(0);
        assertThat(firstTransportLeg.getStatus()).isEqualTo(TransportLegStatus.COMPLETED);

        TransportLeg secondTransportLeg = transportLegs.get(1);
        assertThat(secondTransportLeg.getStatus()).isEqualTo(TransportLegStatus.COMPLETED);

        assertThat(transportOrders.findById(transportOrder.getId()).get().getStatus().equals(TransportStatus.TRANSPORT_COMPLETED));
    }
}
