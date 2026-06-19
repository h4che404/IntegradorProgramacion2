package integrado.prog2.enums;

import java.util.Locale;

public enum FormaPago {
    TARJETA("Tarjeta"),
    TRANSFERENCIA("Transferencia"),
    EFECTIVO("Efectivo");

    private final String label;

    FormaPago(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static FormaPago fromDatabase(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La forma de pago almacenada es inválida.");
        }

        return switch (value.trim().toUpperCase(Locale.ROOT)) {
            case "TARJETA", "CARD" -> TARJETA;
            case "TRANSFERENCIA", "BANK_TRANSFER" -> TRANSFERENCIA;
            case "EFECTIVO", "CASH" -> EFECTIVO;
            default -> throw new IllegalArgumentException("Forma de pago desconocida: " + value);
        };
    }
}
