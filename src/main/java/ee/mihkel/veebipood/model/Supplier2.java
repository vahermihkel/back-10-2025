package ee.mihkel.veebipood.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class Supplier2 {
    private int id;
    private String title;
    private String slug;
    private int price;
    private String description;
    private Supplier2Category category;
    private ArrayList<String> images;
    private Date creationAt;
    private Date updatedAt;
}
