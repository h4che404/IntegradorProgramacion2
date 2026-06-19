# Food Store – Console Order Management with JDBC

Java 21 console application for the Programming 2 integrative assignment. The domain model now aligns with the UML/assignment naming: `Categoria`, `Producto`, `Usuario`, `Pedido`, `DetallePedido`, `Rol`, `Estado`, and `FormaPago`.

## Quick path

1. Create a MySQL database and run `schema.sql`.
2. Update `src/main/resources/persistence.xml` with your local MySQL credentials.
3. Verify the project with `mvn test`.
4. Run the app with `mvn exec:java`.

## What is included

| Area | Decision |
|------|----------|
| Build | Maven project targeting Java 21 |
| Persistence | Plain JDBC with `PreparedStatement`, `ResultSet`, and try-with-resources |
| Structure | `config`, `entities`, `enums`, `interfaces`, `dao`, `service`, `exception`, `ui` |
| Delete strategy | Soft delete (`eliminado = true`) for all delete actions |
| Orders | Transactional insert for `pedido` + `detalle_pedido` with rollback on failure |
| Domain names | Spanish UML-aligned entity and enum names inside the same package structure |

## Project structure

```text
src/main/java/integrado/prog2
├── Main.java
├── config/
├── dao/
├── entities/
├── enums/
├── exception/
├── interfaces/
├── service/
└── ui/
```

## Database setup

Run the SQL script:

```sql
SOURCE schema.sql;
```

The script creates these tables:

- `categoria`
- `producto`
- `usuario`
- `pedido`
- `detalle_pedido`

It also inserts seed data for quick manual testing.

## Configuration

Edit `src/main/resources/persistence.xml`:

```xml
<property name="db.url" value="jdbc:mysql://localhost:3306/pedidos_db?useSSL=false&amp;allowPublicKeyRetrieval=true&amp;serverTimezone=UTC"/>
<property name="db.username" value="root"/>
<property name="db.password" value="root"/>
```

The application reads this file through `PersistenceConfig` and `ConnectionFactory`, so credentials stay centralized.

## Console behavior

- Main menu for Categorías, Productos, Usuarios, and Pedidos.
- CRUD submenus for each module.
- Input validation for bad numbers, invalid options, missing records, and non-positive pedido detail quantities.
- Optional filters when listing products and orders.
- Clear success/error messages after each operation.

## Verification

Run tests and compile:

```bash
mvn test
```

Run the program:

```bash
mvn exec:java
```

## Academic delivery checklist

- [x] Java project organized by packages
- [x] `schema.sql` with seed data
- [x] Centralized DB configuration in `persistence.xml`
- [x] `README.md` with setup and run instructions
- [ ] Replace the placeholder credentials with your local MySQL values
- [ ] Add the public video demo link before submission
- [ ] Add the PDF documentation file or public PDF link before submission

## Submission links

- Video demo: `TODO - replace with public link`
- Academic PDF: `TODO - replace with public link or add the PDF file to the repository root`
