package integrado.prog2.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Producto extends BaseEntity {
    private String name;
    private Double price;
    private String description;
    private int stock;
    private String image;
    private boolean available;
    private Categoria category;

    public Producto() {
        super();
    }

    public Producto(String name, Double price, String description, int stock, String image, boolean available, Categoria category) {
        super();
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.image = image;
        this.available = available;
        this.category = category;
    }

    public Producto(Long id, boolean eliminado, LocalDateTime createdAt, String name, Double price, String description,
                    int stock, String image, boolean available, Categoria category) {
        super(id, eliminado, createdAt);
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.image = image;
        this.available = available;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Categoria getCategory() {
        return category;
    }

    public void setCategory(Categoria category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | Precio: %.2f | Stock: %d | Disponible: %s | Categoría: %s",
                getId(),
                name,
                price,
                stock,
                available ? "Sí" : "No",
                category != null ? category.getName() : "N/A");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Producto product)) {
            return false;
        }
        return Objects.equals(getId(), product.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
