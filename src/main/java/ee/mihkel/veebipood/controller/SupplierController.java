package ee.mihkel.veebipood.controller;

import ee.mihkel.veebipood.model.ParcelMachines;
import ee.mihkel.veebipood.model.Supplier1;
import ee.mihkel.veebipood.model.Supplier2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SupplierController {
//    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("supplier1")
    public List<Supplier1> supplier1(){
        System.out.println(restTemplate);

        String url = "https://fakestoreapi.com/products";
        Supplier1[] body = restTemplate
                .exchange(url, HttpMethod.GET,null,Supplier1[].class).getBody();
       // return Arrays.asList(body.strea);
       return Arrays.stream(body)
               .filter(e -> e.getRating().getRate() > 4.0)
               .filter(e -> e.getRating().getCount() > 100)
//               .sorted(e -> Comparator.comparingDouble(e.getPrice())
               .toList();
    }

    @GetMapping("supplier2")
    public List<Supplier2> supplier2(){
//        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.escuelajs.co/api/v1/products";
        Supplier2[] body = restTemplate
                .exchange(url, HttpMethod.GET,null,Supplier2[].class).getBody();
        // return Arrays.asList(body.strea);
        return Arrays.stream(body)
                .filter(e -> e.getPrice() > 60.0)
//                .filter(e -> new Date(e.getUpdatedAt()).getYear() == 2025)
                .toList();
    }

    @GetMapping("parcelmachines")
    public List<ParcelMachines> parcelmachines(@RequestParam(required = false) String country){
//        RestTemplate restTemplate = new RestTemplate();

        String url = "https://www.omniva.ee/locations.json";
        ParcelMachines[] body = restTemplate
                .exchange(url, HttpMethod.GET,null,ParcelMachines[].class).getBody();
        // return Arrays.asList(body.strea);

        if (country == null) {
            return Arrays.asList(body);
        } else {
            String pmCountry = country.toUpperCase();
            return Arrays.stream(body)
                    .filter(e -> e.getA0_NAME().equals(pmCountry))
                    .toList();
        }
    }
}
