package ee.mihkel.veebipood.controller;

import ee.mihkel.veebipood.entity.Order;
import ee.mihkel.veebipood.entity.Person;
import ee.mihkel.veebipood.entity.Product;
import ee.mihkel.veebipood.model.everypay.EveryPayLink;
import ee.mihkel.veebipood.repository.OrderRepository;
import ee.mihkel.veebipood.repository.PersonRepository;
import ee.mihkel.veebipood.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    PersonRepository personRepository;

    @GetMapping("orders")
    public List<Order> getOrders(){
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Person person = personRepository.findById(id).orElseThrow();
        return orderRepository.findAllByPerson(person);
    }

    @PostMapping("orders")
    public EveryPayLink createOrder(@RequestBody List<Product> products, @RequestParam String parcelMachine){
        return orderService.saveOrder(products, parcelMachine);
    }

}
