package integrado.prog2.enums;

public enum Rol {
    ADMIN("Administrador"),
    USER("Usuario");

    private final String label;

    Rol(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
