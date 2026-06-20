package integrado.prog2.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Categoria extends BaseEntity {
    private String name;
    private String description;
    private List<Producto> products;

    public Categoria() {
        super();
        this.products = new ArrayList<>();
    }

    public Categoria(String name, String description) {
        super();
        this.name = name;
        this.description = description;
        this.products = new ArrayList<>();
    }

    public Categoria(Long id, boolean eliminado, LocalDateTime createdAt, String name, String description) {
        super(id, eliminado, createdAt);
        this.name = name;
        this.description = description;
        this.products = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Producto> getProducts() {
        return products;
    }

    public void setProducts(List<Producto> products) {
        this.products = products != null ? new ArrayList<>(products) : new ArrayList<>();
    }

    public void addProduct(Producto product) {
        if (product != null) {
            products.add(product);
        }
    }

    public void removeProduct(Producto product) {
        if (product != null) {
            products.remove(product);
        }
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s", getId(), name, description == null ? "" : description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Categoria category)) {
            return false;
        }
        return Objects.equals(getId(), category.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
