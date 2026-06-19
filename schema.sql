CREATE DATABASE IF NOT EXISTS pedidos_db;
USE pedidos_db;

DROP TABLE IF EXISTS detalle_pedido;
DROP TABLE IF EXISTS pedido;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS categoria;

CREATE TABLE categoria (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE producto (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    stock INT NOT NULL,
    imagen VARCHAR(255) NOT NULL,
    disponible BOOLEAN NOT NULL,
    categoria_id BIGINT NOT NULL,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

CREATE TABLE usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    mail VARCHAR(120) NOT NULL UNIQUE,
    celular VARCHAR(40) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pedido (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fecha DATE NOT NULL,
    estado VARCHAR(30) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    forma_pago VARCHAR(30) NOT NULL,
    usuario_id BIGINT NOT NULL,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE detalle_pedido (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES producto(id)
);

INSERT INTO categoria (nombre, descripcion) VALUES
('Pizzas', 'Classic and specialty pizzas'),
('Bebidas', 'Cold drinks and soft drinks'),
('Postres', 'Desserts and sweet options');

INSERT INTO producto (nombre, precio, descripcion, stock, imagen, disponible, categoria_id) VALUES
('Muzzarella', 8500.00, 'Large mozzarella pizza', 20, 'muzzarella.jpg', TRUE, 1),
('Napolitana', 9700.00, 'Tomato, garlic and mozzarella pizza', 12, 'napolitana.jpg', TRUE, 1),
('Cola 1.5L', 3200.00, '1.5 liter soda bottle', 35, 'cola.jpg', TRUE, 2),
('Flan casero', 2800.00, 'Homemade flan with dulce de leche', 10, 'flan.jpg', TRUE, 3);

INSERT INTO usuario (nombre, apellido, mail, celular, password, rol) VALUES
('Juan', 'Pérez', 'juan.perez@example.com', '1122334455', 'admin123', 'ADMIN'),
('Ana', 'Gómez', 'ana.gomez@example.com', '1199887766', 'user123', 'USER');

INSERT INTO pedido (fecha, estado, total, forma_pago, usuario_id) VALUES
('2026-06-15', 'CONFIRMADO', 11700.00, 'EFECTIVO', 2);

INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, subtotal) VALUES
(1, 1, 1, 8500.00),
(1, 3, 1, 3200.00);
