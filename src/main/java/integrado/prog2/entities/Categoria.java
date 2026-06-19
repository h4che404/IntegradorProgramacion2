package integrado.prog2.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Categoria extends BaseEntity {
    private String name;
    private String description;

    public Categoria() {
        super();
    }

    public Categoria(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    public Categoria(Long id, boolean eliminado, LocalDateTime createdAt, String name, String description) {
        super(id, eliminado, createdAt);
        this.name = name;
        this.description = description;
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
