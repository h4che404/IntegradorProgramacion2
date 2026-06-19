package integrado.prog2;

import integrado.prog2.config.ConnectionFactory;
import integrado.prog2.dao.CategoryDao;
import integrado.prog2.dao.OrderDao;
import integrado.prog2.dao.ProductDao;
import integrado.prog2.dao.UserDao;
import integrado.prog2.exception.DataAccessException;
import integrado.prog2.service.CategoryService;
import integrado.prog2.service.OrderService;
import integrado.prog2.service.ProductService;
import integrado.prog2.service.UserService;
import integrado.prog2.ui.ConsoleApp;
import integrado.prog2.ui.InputReader;

public class Main {
    public static void main(String[] args) {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();

            CategoryDao categoryDao = new CategoryDao(connectionFactory);
            ProductDao productDao = new ProductDao(connectionFactory);
            UserDao userDao = new UserDao(connectionFactory);
            OrderDao orderDao = new OrderDao(connectionFactory);

            CategoryService categoryService = new CategoryService(categoryDao);
            ProductService productService = new ProductService(productDao, categoryService);
            UserService userService = new UserService(userDao);
            OrderService orderService = new OrderService(orderDao, userService, productService);

            ConsoleApp consoleApp = new ConsoleApp(
                    categoryService,
                    productService,
                    userService,
                    orderService,
                    new InputReader()
            );
            consoleApp.run();
        } catch (DataAccessException exception) {
            System.out.println("No se pudo iniciar la aplicación: " + exception.getMessage());
        }
    }
}
