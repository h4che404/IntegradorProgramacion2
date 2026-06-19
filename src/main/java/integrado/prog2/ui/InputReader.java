package integrado.prog2.ui;

import java.util.Scanner;

public class InputReader {
    private final Scanner scanner;

    public InputReader() {
        this.scanner = new Scanner(System.in);
    }

    public int readMenuOption(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                int option = Integer.parseInt(value);
                if (option < min || option > max) {
                    System.out.println("Opción inválida. Intentá nuevamente.");
                    continue;
                }
                return option;
            } catch (NumberFormatException exception) {
                System.out.println("Ingresá un número válido.");
            }
        }
    }

    public String readRequiredText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("El valor no puede estar vacío.");
        }
    }

    public String readOptionalText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public Long readRequiredLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException exception) {
                System.out.println("Ingresá un número entero válido.");
            }
        }
    }

    public Long readOptionalLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (value.isBlank()) {
                return null;
            }
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException exception) {
                System.out.println("Ingresá un número entero válido o dejá vacío.");
            }
        }
    }

    public int readRequiredInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Ingresá un número entero válido.");
            }
        }
    }

    public int readRequiredPositiveInt(String prompt, String validationMessage) {
        while (true) {
            int value = readRequiredInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println(validationMessage);
        }
    }

    public Integer readOptionalInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (value.isBlank()) {
                return null;
            }
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Ingresá un número entero válido o dejá vacío.");
            }
        }
    }

    public double readRequiredDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException exception) {
                System.out.println("Ingresá un número decimal válido.");
            }
        }
    }

    public Double readOptionalDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim().replace(',', '.');
            if (value.isBlank()) {
                return null;
            }
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException exception) {
                System.out.println("Ingresá un número decimal válido o dejá vacío.");
            }
        }
    }

    public boolean readConfirmation(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim().toUpperCase();
            if ("S".equals(value)) {
                return true;
            }
            if ("N".equals(value)) {
                return false;
            }
            System.out.println("Ingresá S o N.");
        }
    }

    public Boolean readOptionalBoolean(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim().toUpperCase();
            if (value.isBlank()) {
                return null;
            }
            if ("S".equals(value)) {
                return true;
            }
            if ("N".equals(value)) {
                return false;
            }
            System.out.println("Ingresá S, N o dejá vacío.");
        }
    }

    public void pause() {
        System.out.println("Presioná Enter para continuar...");
        scanner.nextLine();
    }
}
