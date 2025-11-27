package ee.mihkel.veebipood.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double price;
    private String image;
    private boolean active;
    private int stock;

    // parem pool tähistab, kas on muutuja ees on List<> või mitte
    // @OneToOne
    // @ManyToOne
    // @OneToMany
    // @ManyToMany
    // vasak pool tähistab, kas seda kategooriat saavad kasutada ka teised tooted
    @ManyToOne
    private Category category;
}
