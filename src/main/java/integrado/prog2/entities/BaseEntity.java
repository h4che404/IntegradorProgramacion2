package integrado.prog2.entities;

import java.time.LocalDateTime;

public abstract class BaseEntity {
    private Long id;
    private boolean eliminado;
    private LocalDateTime createdAt;

    protected BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.eliminado = false;
    }

    protected BaseEntity(Long id, boolean eliminado, LocalDateTime createdAt) {
        this.id = id;
        this.eliminado = eliminado;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
