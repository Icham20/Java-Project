CREATE TABLE IF NOT EXISTS category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category_id INT,
    image_url VARCHAR(255),
    is_spicy BOOLEAN DEFAULT FALSE,
    is_vegetarian BOOLEAN DEFAULT FALSE,
    available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100),
    total_price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    options VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Données de test
INSERT INTO category (name) VALUES ('Entrées'), ('Plats'), ('Desserts');

INSERT INTO product (name, description, price, category_id, image_url, is_spicy, is_vegetarian) VALUES 
('Nems au Poulet', '3 pièces, servis avec sauce nuoc-mâm', 5.50, 1, 'nems.jpg', FALSE, FALSE),
('Rouleaux de Printemps', '2 pièces, crevettes et menthe fraîche', 4.90, 1, 'rouleaux.jpg', FALSE, TRUE),
('Boeuf aux Oignons', 'Sauté au wok, tendre et parfumé', 12.50, 2, 'boeuf_oignons.jpg', FALSE, FALSE),
('Porc au Caramel', 'Classique vietnamien', 11.90, 2, 'porc_caramel.jpg', FALSE, FALSE),
('Poulet au Curry Rouge', 'Épicé et crémeux, lait de coco', 13.50, 2, 'poulet_curry.jpg', TRUE, FALSE),
('Perles de Coco', '2 pièces, chaud', 4.00, 3, 'perles_coco.jpg', FALSE, TRUE);