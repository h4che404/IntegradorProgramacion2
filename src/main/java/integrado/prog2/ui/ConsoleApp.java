package integrado.prog2.ui;

import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.DetallePedido;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Producto;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.enums.Rol;
import integrado.prog2.exception.DataAccessException;
import integrado.prog2.exception.DuplicateEntityException;
import integrado.prog2.exception.EntityNotFoundException;
import integrado.prog2.exception.ValidationException;
import integrado.prog2.service.CategoryService;
import integrado.prog2.service.OrderService;
import integrado.prog2.service.ProductService;
import integrado.prog2.service.UserService;

import java.time.LocalDate;
import java.util.List;

public class ConsoleApp {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final InputReader inputReader;

    public ConsoleApp(CategoryService categoryService, ProductService productService, UserService userService,
                      OrderService orderService, InputReader inputReader) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.userService = userService;
        this.orderService = orderService;
        this.inputReader = inputReader;
    }

    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== SISTEMA DE PEDIDOS (FOOD STORE) ===");
            System.out.println("1. Categorías");
            System.out.println("2. Productos");
            System.out.println("3. Usuarios");
            System.out.println("4. Pedidos");
            System.out.println("5. Probar conexión a la BD");
            System.out.println("0. Salir");

            int option = inputReader.readMenuOption("Seleccione: ", 0, 5);
            switch (option) {
                case 1 -> showCategoryMenu();
                case 2 -> showProductMenu();
                case 3 -> showUserMenu();
                case 4 -> showOrderMenu();
                case 5 -> testConnection();
                case 0 -> {
                    running = false;
                    System.out.println("¡Hasta luego!");
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void testConnection() {
        System.out.println("\n[📡 Probando conexión a la base de datos...]");
        try {
            categoryService.findAll();
            System.out.println("✅ ¡Conexión exitosa! Java y MySQL se están comunicando perfectamente.");
        } catch (Exception e) {
            System.out.println("❌ Error de comunicación: " + e.getMessage());
            System.out.println("👉 Verificá que MySQL esté prendido y que persistence.xml tenga tu usuario/clave.");
        }
    }

    private void showCategoryMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Categorías ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Editar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");

            switch (inputReader.readMenuOption("Seleccione: ", 0, 4)) {
                case 1 -> execute(this::listCategories);
                case 2 -> execute(this::createCategory);
                case 3 -> execute(this::editCategory);
                case 4 -> execute(this::deleteCategory);
                case 0 -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void showProductMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Productos ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Editar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");

            switch (inputReader.readMenuOption("Seleccione: ", 0, 4)) {
                case 1 -> execute(this::listProducts);
                case 2 -> execute(this::createProduct);
                case 3 -> execute(this::editProduct);
                case 4 -> execute(this::deleteProduct);
                case 0 -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void showUserMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Usuarios ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Editar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");

            switch (inputReader.readMenuOption("Seleccione: ", 0, 4)) {
                case 1 -> execute(this::listUsers);
                case 2 -> execute(this::createUser);
                case 3 -> execute(this::editUser);
                case 4 -> execute(this::deleteUser);
                case 0 -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void showOrderMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Pedidos ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Editar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");

            switch (inputReader.readMenuOption("Seleccione: ", 0, 4)) {
                case 1 -> execute(this::listOrders);
                case 2 -> execute(this::createOrder);
                case 3 -> execute(this::editOrder);
                case 4 -> execute(this::deleteOrder);
                case 0 -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void listCategories() {
        List<Categoria> categories = categoryService.findAll();
        if (categories.isEmpty()) {
            System.out.println("No hay categorías cargadas.");
            return;
        }
        printCategories(categories);
    }

    private void createCategory() {
        String name = inputReader.readRequiredText("Nombre: ");
        String description = inputReader.readRequiredText("Descripción: ");
        Categoria category = new Categoria(name, description);
        Categoria savedCategory = categoryService.create(category);
        System.out.println("Categoría creada con ID: " + savedCategory.getId());
    }

    private void editCategory() {
        listCategories();
        Long id = inputReader.readRequiredLong("ID de la categoría a editar: ");
        Categoria category = categoryService.findById(id);

        String name = inputReader.readOptionalText("Nombre [" + category.getName() + "]: ");
        String description = inputReader.readOptionalText("Descripción [" + category.getDescription() + "]: ");

        if (!name.isBlank()) {
            category.setName(name);
        }
        if (!description.isBlank()) {
            category.setDescription(description);
        }

        categoryService.update(category);
        System.out.println("Categoría actualizada correctamente.");
    }

    private void deleteCategory() {
        listCategories();
        Long id = inputReader.readRequiredLong("ID de la categoría a eliminar: ");
        if (inputReader.readConfirmation("¿Confirmás la eliminación lógica? (S/N): ")) {
            categoryService.delete(id);
            System.out.println("Categoría eliminada correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void listProducts() {
        Long categoryId = inputReader.readOptionalLong("ID de categoría para filtrar (Enter para listar todo): ");
        List<Producto> products = categoryId == null ? productService.findAll() : productService.findByCategory(categoryId);

        if (products.isEmpty()) {
            System.out.println("No hay productos cargados.");
            return;
        }
        printProducts(products);
    }

    private void createProduct() {
        ensureCategoriesExist();
        listCategories();

        String name = inputReader.readRequiredText("Nombre: ");
        String description = inputReader.readRequiredText("Descripción: ");
        double price = inputReader.readRequiredDouble("Precio: ");
        int stock = inputReader.readRequiredInt("Stock: ");
        String image = inputReader.readRequiredText("Imagen/URL: ");
        boolean available = inputReader.readConfirmation("¿Disponible? (S/N): ");
        Long categoryId = inputReader.readRequiredLong("ID de la categoría: ");

        Producto product = new Producto(name, price, description, stock, image, available, new Categoria());
        product.getCategory().setId(categoryId);

        Producto savedProduct = productService.create(product);
        System.out.println("Producto creado con ID: " + savedProduct.getId());
    }

    private void editProduct() {
        listProducts();
        Long id = inputReader.readRequiredLong("ID del producto a editar: ");
        Producto product = productService.findById(id);

        String name = inputReader.readOptionalText("Nombre [" + product.getName() + "]: ");
        String description = inputReader.readOptionalText("Descripción [" + product.getDescription() + "]: ");
        Double price = inputReader.readOptionalDouble("Precio [" + product.getPrice() + "]: ");
        Integer stock = inputReader.readOptionalInt("Stock [" + product.getStock() + "]: ");
        String image = inputReader.readOptionalText("Imagen/URL [" + product.getImage() + "]: ");
        Boolean available = inputReader.readOptionalBoolean("¿Disponible? [" + (product.isAvailable() ? "S" : "N") + "] (S/N o Enter): ");
        Long categoryId = inputReader.readOptionalLong("ID de la categoría [" + product.getCategory().getId() + "] (Enter para mantener): ");

        if (!name.isBlank()) {
            product.setName(name);
        }
        if (!description.isBlank()) {
            product.setDescription(description);
        }
        if (price != null) {
            product.setPrice(price);
        }
        if (stock != null) {
            product.setStock(stock);
        }
        if (!image.isBlank()) {
            product.setImage(image);
        }
        if (available != null) {
            product.setAvailable(available);
        }
        if (categoryId != null) {
            Categoria category = new Categoria();
            category.setId(categoryId);
            product.setCategory(category);
        }

        productService.update(product);
        System.out.println("Producto actualizado correctamente.");
    }

    private void deleteProduct() {
        listProducts();
        Long id = inputReader.readRequiredLong("ID del producto a eliminar: ");
        if (inputReader.readConfirmation("¿Confirmás la eliminación lógica? (S/N): ")) {
            productService.delete(id);
            System.out.println("Producto eliminado correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void listUsers() {
        List<Usuario> users = userService.findAll();
        if (users.isEmpty()) {
            System.out.println("No hay usuarios cargados.");
            return;
        }
        printUsers(users);
    }

    private void createUser() {
        String firstName = inputReader.readRequiredText("Nombre: ");
        String lastName = inputReader.readRequiredText("Apellido: ");
        String mail = inputReader.readRequiredText("Mail: ");
        String phone = inputReader.readRequiredText("Celular: ");
        String password = inputReader.readRequiredText("Contraseña: ");
        Rol role = selectRole(null);

        Usuario user = new Usuario(firstName, lastName, mail, phone, password, role);
        Usuario savedUser = userService.create(user);
        System.out.println("Usuario creado con ID: " + savedUser.getId());
    }

    private void editUser() {
        listUsers();
        Long id = inputReader.readRequiredLong("ID del usuario a editar: ");
        Usuario user = userService.findById(id);

        String firstName = inputReader.readOptionalText("Nombre [" + user.getFirstName() + "]: ");
        String lastName = inputReader.readOptionalText("Apellido [" + user.getLastName() + "]: ");
        String mail = inputReader.readOptionalText("Mail [" + user.getMail() + "]: ");
        String phone = inputReader.readOptionalText("Celular [" + user.getPhone() + "]: ");
        String password = inputReader.readOptionalText("Contraseña [oculta] (Enter para mantener): ");
        Rol role = selectRole(user.getRole());

        if (!firstName.isBlank()) {
            user.setFirstName(firstName);
        }
        if (!lastName.isBlank()) {
            user.setLastName(lastName);
        }
        if (!mail.isBlank()) {
            user.setMail(mail);
        }
        if (!phone.isBlank()) {
            user.setPhone(phone);
        }
        if (!password.isBlank()) {
            user.setPassword(password);
        }
        user.setRole(role);

        userService.update(user);
        System.out.println("Usuario actualizado correctamente.");
    }

    private void deleteUser() {
        listUsers();
        Long id = inputReader.readRequiredLong("ID del usuario a eliminar: ");
        if (inputReader.readConfirmation("¿Confirmás la eliminación lógica? (S/N): ")) {
            userService.delete(id);
            System.out.println("Usuario eliminado correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void listOrders() {
        Long userId = inputReader.readOptionalLong("ID de usuario para filtrar (Enter para listar todo): ");
        List<Pedido> orders = orderService.findAllByUserId(userId);

        if (orders.isEmpty()) {
            System.out.println("No hay pedidos cargados.");
            return;
        }
        printOrders(orders);
    }

    private void createOrder() {
        ensureUsersExist();
        ensureProductsExist();

        listUsers();
        Long userId = inputReader.readRequiredLong("ID del usuario: ");
        FormaPago paymentMethod = selectPaymentMethod(null);
        Estado status = selectOrderStatus(Estado.PENDIENTE);

        Pedido order = new Pedido();
        Usuario user = new Usuario();
        user.setId(userId);
        order.setUser(user);
        order.setDate(LocalDate.now());
        order.setPaymentMethod(paymentMethod);
        order.setStatus(status);

        boolean addMore = true;
        while (addMore) {
            printProducts(productService.findAll());
            Long productId = inputReader.readRequiredLong("ID del producto: ");
            Producto product = productService.findById(productId);
            int quantity = inputReader.readRequiredPositiveInt("Cantidad: ", "La cantidad debe ser mayor a cero.");
            order.addDetallePedido(quantity, product.getPrice(), product);
            addMore = inputReader.readConfirmation("¿Querés agregar otro producto? (S/N): ");
        }

        Pedido savedOrder = orderService.create(order);
        System.out.println("Pedido creado con ID: " + savedOrder.getId() + " | Total: " + savedOrder.getTotal());
    }

    private void editOrder() {
        List<Pedido> orders = orderService.findAll();
        if (orders.isEmpty()) {
            System.out.println("No hay pedidos cargados.");
            return;
        }
        printOrders(orders);
        Long id = inputReader.readRequiredLong("ID del pedido a editar: ");
        Pedido order = orderService.findById(id);

        Estado status = selectOrderStatus(order.getStatus());
        FormaPago paymentMethod = selectPaymentMethod(order.getPaymentMethod());

        Pedido updateOrder = new Pedido();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(status);
        updateOrder.setPaymentMethod(paymentMethod);

        orderService.update(updateOrder);
        System.out.println("Pedido actualizado correctamente.");
    }

    private void deleteOrder() {
        List<Pedido> orders = orderService.findAll();
        if (orders.isEmpty()) {
            System.out.println("No hay pedidos cargados.");
            return;
        }
        printOrders(orders);
        Long id = inputReader.readRequiredLong("ID del pedido a eliminar: ");
        if (inputReader.readConfirmation("¿Confirmás la eliminación lógica? (S/N): ")) {
            orderService.delete(id);
            System.out.println("Pedido eliminado correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private Rol selectRole(Rol currentRole) {
        Rol[] roles = Rol.values();
        while (true) {
            System.out.println("Roles disponibles:");
            for (int i = 0; i < roles.length; i++) {
                System.out.println((i + 1) + ". " + roles[i].getLabel());
            }

            String prompt = currentRole == null
                    ? "Seleccione rol: "
                    : "Seleccione rol [Enter para mantener " + currentRole.getLabel() + "]: ";
            String value = inputReader.readOptionalText(prompt);

            if (value.isBlank() && currentRole != null) {
                return currentRole;
            }

            try {
                int option = Integer.parseInt(value);
                if (option >= 1 && option <= roles.length) {
                    return roles[option - 1];
                }
            } catch (NumberFormatException ignored) {
                // handled below
            }
            System.out.println("Opción inválida.");
        }
    }

    private Estado selectOrderStatus(Estado currentStatus) {
        Estado[] statuses = Estado.values();
        while (true) {
            System.out.println("Estados disponibles:");
            for (int i = 0; i < statuses.length; i++) {
                System.out.println((i + 1) + ". " + statuses[i].getLabel());
            }

            String prompt = currentStatus == null
                    ? "Seleccione estado: "
                    : "Seleccione estado [Enter para mantener " + currentStatus.getLabel() + "]: ";
            String value = inputReader.readOptionalText(prompt);

            if (value.isBlank() && currentStatus != null) {
                return currentStatus;
            }

            try {
                int option = Integer.parseInt(value);
                if (option >= 1 && option <= statuses.length) {
                    return statuses[option - 1];
                }
            } catch (NumberFormatException ignored) {
                // handled below
            }
            System.out.println("Opción inválida.");
        }
    }

    private FormaPago selectPaymentMethod(FormaPago currentPaymentMethod) {
        FormaPago[] methods = FormaPago.values();
        while (true) {
            System.out.println("Formas de pago disponibles:");
            for (int i = 0; i < methods.length; i++) {
                System.out.println((i + 1) + ". " + methods[i].getLabel());
            }

            String prompt = currentPaymentMethod == null
                    ? "Seleccione forma de pago: "
                    : "Seleccione forma de pago [Enter para mantener " + currentPaymentMethod.getLabel() + "]: ";
            String value = inputReader.readOptionalText(prompt);

            if (value.isBlank() && currentPaymentMethod != null) {
                return currentPaymentMethod;
            }

            try {
                int option = Integer.parseInt(value);
                if (option >= 1 && option <= methods.length) {
                    return methods[option - 1];
                }
            } catch (NumberFormatException ignored) {
                // handled below
            }
            System.out.println("Opción inválida.");
        }
    }

    private void ensureCategoriesExist() {
        if (categoryService.findAll().isEmpty()) {
            throw new ValidationException("Debés crear al menos una categoría primero.");
        }
    }

    private void ensureProductsExist() {
        if (productService.findAll().isEmpty()) {
            throw new ValidationException("Debés crear al menos un producto primero.");
        }
    }

    private void ensureUsersExist() {
        if (userService.findAll().isEmpty()) {
            throw new ValidationException("Debés crear al menos un usuario primero.");
        }
    }

    private void printCategories(List<Categoria> categories) {
        categories.forEach(System.out::println);
    }

    private void printProducts(List<Producto> products) {
        products.forEach(System.out::println);
    }

    private void printUsers(List<Usuario> users) {
        users.forEach(System.out::println);
    }

    private void printOrders(List<Pedido> orders) {
        for (Pedido order : orders) {
            System.out.println(order);
            for (DetallePedido detail : order.getDetails()) {
                System.out.println("   " + detail);
            }
        }
    }

    private void execute(Runnable action) {
        try {
            action.run();
        } catch (ValidationException | DuplicateEntityException | EntityNotFoundException exception) {
            System.out.println("Error: " + exception.getMessage());
        } catch (DataAccessException exception) {
            System.out.println("Error de persistencia: " + exception.getMessage());
        } catch (Exception exception) {
            System.out.println("Ocurrió un error inesperado: " + exception.getMessage());
        } finally {
            inputReader.pause();
        }
    }
}
