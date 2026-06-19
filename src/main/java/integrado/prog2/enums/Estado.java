package integrado.prog2.enums;

import java.util.Locale;

public enum Estado {
    PENDIENTE("Pendiente"),
    CONFIRMADO("Confirmado"),
    TERMINADO("Terminado"),
    CANCELADO("Cancelado");

    private final String label;

    Estado(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Estado fromDatabase(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El estado almacenado es inválido.");
        }

        return switch (value.trim().toUpperCase(Locale.ROOT)) {
            case "PENDIENTE", "PENDING" -> PENDIENTE;
            case "CONFIRMADO", "CONFIRMED" -> CONFIRMADO;
            case "TERMINADO", "COMPLETED" -> TERMINADO;
            case "CANCELADO", "CANCELLED" -> CANCELADO;
            default -> throw new IllegalArgumentException("Estado desconocido: " + value);
        };
    }
}
