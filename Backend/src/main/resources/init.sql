SET NAMES utf8mb4;

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
    available BOOLEAN DEFAULT TRUE,
    stock_quantity INT DEFAULT 50,
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
INSERT INTO category (name) VALUES ('Entrées'), ('Plats'), ('Desserts'), ('Boissons');

INSERT INTO product (name, description, price, category_id, image_url, is_spicy) VALUES 
('Nems au Poulet', '3 pièces, servis avec sauce nuoc-mâm', 5.50, 1, 'nems.jpg', FALSE),
('Rouleaux de Printemps', '2 pièces, crevettes et menthe fraîche', 4.90, 1, 'rouleaux.jpg', FALSE),
('Salade de Papaye', 'Verte, épicée et croquante', 8.50, 1, 'papaye.jpg', TRUE),
('Samoussas Bœuf', '3 pièces, croustillants épices douces', 5.00, 1, 'samoussa.jpg', FALSE),
('Boeuf aux Oignons', 'Sauté au wok, tendre et parfumé', 12.50, 2, 'boeuf_oignons.jpg', FALSE),
('Porc au Caramel', 'Classique vietnamien', 11.90, 2, 'porc_caramel.jpg', FALSE),
('Poulet au Curry Rouge', 'Épicé et crémeux, lait de coco', 13.50, 2, 'poulet_curry.jpg', TRUE),
('Bo Bun', 'Vermicelles, bœuf sauté, nems et crudités', 11.00, 2, 'bobun.jpg', FALSE),
('Pho Spécial', 'Soupe traditionnelle bœuf et boulettes', 12.00, 2, 'pho.jpg', FALSE),
('Canard Laqué', 'Servi avec riz cantonais', 14.50, 2, 'canard.jpg', FALSE),
('Pad Thaï Crevettes', 'Nouilles de riz sautées aux crevettes', 13.00, 2, 'padthai.jpg', TRUE),
('Perles de Coco', '2 pièces, chaud', 4.00, 3, 'perles_coco.jpg', FALSE),
('Mangue Fraîche', 'Découpée, très parfumée', 5.50, 3, 'mangue.jpg', FALSE),
('Nougat au Sésame', 'Portion de 4 pièces', 3.50, 3, 'nougat.jpg', FALSE),
('Coca-Cola', '33cl, bien frais', 3.00, 4, 'coca.jpg', FALSE),
('Thé Glacé Maison', 'Citron et menthe', 3.50, 4, 'the_glace.jpg', FALSE),
('Bière Saigon', 'Bière blonde vietnamienne', 5.00, 4, 'biere_saigon.jpg', FALSE),
('Eau Minérale', '50cl', 2.00, 4, 'eau.jpg', FALSE),
('Jus de Litchi', 'Frais et sucré', 3.50, 4, 'jus_litchi.jpg', FALSE),
('Café Vietnamien', 'Au lait concentré', 4.00, 4, 'cafe_viet.jpg', FALSE);