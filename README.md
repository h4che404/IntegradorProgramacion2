# Food Store – Console Order Management with JDBC

Console-based Java 21 application for the Programming 2 integrative assignment. The project manages categories, products, users, and food orders using a layered structure (`ui`, `service`, `dao`, `entities`) plus JDBC with MySQL.

## Quick path

1. Create the MySQL database by running `schema.sql`.
2. Update `src/main/resources/persistence.xml` with your local connection values.
3. Build the project with Maven.
4. Run the console application.

## Requirements

| Requirement | Notes |
|-------------|-------|
| Java | JDK 21 |
| Maven | Maven 3.9+ recommended |
| Database | MySQL 8.x |
| JDBC driver | Included through `mysql-connector-j` in `pom.xml` |

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

## Create the database

The repository already includes `schema.sql`. The script:

- creates the database `pedidos_db`
- creates `categoria`, `producto`, `usuario`, `pedido`, and `detalle_pedido`
- inserts seed data for manual testing

You can execute it in either of these ways.

### Option 1: from the MySQL client

```bash
mysql -u root -p < schema.sql
```

### Option 2: inside an open MySQL session

```sql
SOURCE /absolute/path/to/schema.sql;
```

## Configure the connection

Edit `src/main/resources/persistence.xml` and replace the sample values with the credentials from your machine:

```xml
<property name="db.url" value="jdbc:mysql://localhost:3306/pedidos_db?useSSL=false&amp;allowPublicKeyRetrieval=true&amp;serverTimezone=UTC"/>
<property name="db.username" value="root"/>
<property name="db.password" value="root"/>
```

The application reads this file through `PersistenceConfig` and `ConnectionFactory`, so all JDBC configuration stays centralized.

## Run the project

Compile the application:

```bash
mvn clean package
```

Run the console app:

```bash
mvn exec:java
```

## Implementation notes

| Area | Current behavior |
|------|------------------|
| Persistence | JDBC with `PreparedStatement`, `ResultSet`, and try-with-resources |
| Delete strategy | Soft delete through `eliminado = true` |
| Orders | `Pedido` creation inserts `pedido` and `detalle_pedido` inside one transaction with rollback on failure |
| Totals | `Pedido` keeps `Calculable`, `addDetallePedido(...)`, and `calcularTotal()` |
| Update scope for orders | The current console flow updates order status and payment method only; editing order details in-place was left out to avoid risky menu/DAO rewrites |

## Video demo

The repository does not currently include a public demo link.

Before submission, replace this section with the final public URL to the demonstration video (for example, YouTube or Google Drive with viewer access).

## Academic PDF / documentation

The final academic PDF is not currently included in this repository.

Suggested file name:

```text
Food_Store_TPI_Documentation.pdf
```

Suggested location:

```text
./Food_Store_TPI_Documentation.pdf
```

Suggested sections for that PDF:

1. Assignment overview and team members
2. UML/domain model explanation
3. Database schema and relationships
4. Architecture by layers (`ui`, `service`, `dao`, `entities`)
5. CRUD coverage for categories, products, users, and orders
6. Validation and transaction decisions
7. Screenshots of console execution
8. Demo video link
9. Conclusions or lessons learned

If `Consigna_TPI_Prog-2.docx.pdf` is kept as supporting material, treat it only as the assignment statement. It does not replace the final academic report requested for delivery.

## Delivery cleanup note

When preparing the ZIP for submission, include only the source project files required to build and review the work. Do not include local or generated artifacts such as `.git/`, `target/`, `.DS_Store`, `__MACOSX`, or `.atl/`.
