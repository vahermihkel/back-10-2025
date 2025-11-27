package ee.mihkel.veebipood.service;

import ee.mihkel.veebipood.entity.Order;
import ee.mihkel.veebipood.entity.PaymentState;
import ee.mihkel.veebipood.entity.Person;
import ee.mihkel.veebipood.entity.Product;
import ee.mihkel.veebipood.model.everypay.EveryPayBody;
import ee.mihkel.veebipood.model.everypay.EveryPayLink;
import ee.mihkel.veebipood.model.everypay.EveryPayResponse;
import ee.mihkel.veebipood.repository.OrderRepository;
import ee.mihkel.veebipood.repository.PersonRepository;
import ee.mihkel.veebipood.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PersonRepository personRepository;


    public EveryPayLink saveOrder(List<Product> products, String parcelMachine) {
        Order order = new Order();
        order.setCreated(new Date());
        order.setProducts(products);

        double total = 0;
        for (Product product : products){
            Product dbProduct = productRepository.findById(product.getId()).orElseThrow();
            if (!dbProduct.isActive()) {
                throw new RuntimeException("Inactive product bought");
            }
            total += dbProduct.getPrice();
        }

        order.setTotal(total);

        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Person person = personRepository.findById(id).orElseThrow();
        order.setPerson(person);

        order.setParcelMachineName(parcelMachine);
        order.setPaymentState(PaymentState.INITIAL);

        Long orderId = orderRepository.save(order).getId();

        String everyPayUrl = makePayment(orderId, total);
        EveryPayLink  everyPayLink = new EveryPayLink();
        everyPayLink.setPaymentLink(everyPayUrl);
        return everyPayLink;
    }

    private String makePayment(Long orderId, double total) {
        EveryPayBody everyPayBody = new EveryPayBody();
        everyPayBody.setAccount_name("EUR3D1");
        everyPayBody.setNonce("das" + ZonedDateTime.now() + Math.random());
        everyPayBody.setTimestamp(ZonedDateTime.now().toString());
        everyPayBody.setAmount(total);
        everyPayBody.setOrder_reference("acd" + orderId.toString());
        everyPayBody.setCustomer_url("https://err.ee");
        everyPayBody.setApi_username("e36eb40f5ec87fa2");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("e36eb40f5ec87fa2", "7b91a3b9e1b74524c2e9fc282f8ac8cd");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EveryPayBody> httpEntity = new HttpEntity<>(everyPayBody, headers);

        String url = "https://igw-demo.every-pay.com/api/v4/payments/oneoff";
        EveryPayResponse response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, EveryPayResponse.class).getBody();
        if (response == null) {
            throw new RuntimeException("Failed to send payment link");
        }
        return response.getPayment_link();
    }
}
