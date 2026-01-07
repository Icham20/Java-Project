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
-- On définit 3 catégories claires, en regroupant Desserts et Boissons pour respecter l'affichage à 3 onglets
INSERT INTO category (name) VALUES ('Entrées'), ('Plats'), ('Desserts & Boissons');

INSERT INTO product (name, description, price, category_id, image_url, is_spicy, is_vegetarian) VALUES 
-- ENTRÉES
('Nems au Poulet', '3 pièces, servis avec sauce nuoc-mâm et salade', 5.50, 1, 'nems.jpg', FALSE, FALSE),
('Rouleaux de Printemps', '2 pièces, crevettes, menthe fraîche et vermicelles', 4.90, 1, 'rouleaux.jpg', FALSE, TRUE),
('Samoussas aux Légumes', '4 pièces, triangles croustillants aux petits légumes', 4.50, 1, 'samoussas.jpg', FALSE, TRUE),
('Raviolis Crevettes', '4 pièces (Ha Kao), cuits à la vapeur', 6.00, 1, 'hakao.jpg', FALSE, FALSE),
('Salade de Chou', 'Salade croquante aigre-douce', 3.50, 1, 'salade_chou.jpg', FALSE, TRUE),

-- PLATS
('Boeuf aux Oignons', 'Sauté au wok, tendre et parfumé', 12.50, 2, 'boeuf_oignons.jpg', FALSE, FALSE),
('Porc au Caramel', 'Classique vietnamien mijoté longuement', 11.90, 2, 'porc_caramel.jpg', FALSE, FALSE),
('Poulet au Curry Rouge', 'Épicé et crémeux, lait de coco et bambou', 13.50, 2, 'poulet_curry.jpg', TRUE, FALSE),
('Canard Laqué', 'Servi avec sa sauce hoisin et crêpes de riz', 14.90, 2, 'canard.jpg', FALSE, FALSE),
('Pad Thaï Crevettes', 'Nouilles de riz sautées, cacahuètes et citron vert', 13.00, 2, 'padthai.jpg', FALSE, FALSE),
('Bo Bun Boeuf', 'Salade de vermicelles, boeuf sauté et nems', 12.00, 2, 'bobun.jpg', FALSE, FALSE),

-- DESSERTS & BOISSONS
('Perles de Coco', '2 pièces, servi chaud, cœur soja jaune', 4.00, 3, 'perles_coco.jpg', FALSE, TRUE),
('Nougat Chinois', 'Assortiment de nougat mou aux sésames', 3.50, 3, 'nougat.jpg', FALSE, TRUE),
('Mochi Glacé Mangue', '2 pièces, dessert japonais glacé', 5.00, 3, 'mochi.jpg', FALSE, TRUE),
('Litchis au Sirop', 'Bol de litchis frais et parfumés', 3.50, 3, 'litchis.jpg', FALSE, TRUE),
('Coca-Cola', 'Canette 33cl, original', 2.50, 3, 'coca.jpg', FALSE, TRUE),
('Bière Tsingtao', 'Bière blonde chinoise 33cl', 4.00, 3, 'tsingtao.jpg', FALSE, TRUE),
('Thé au Jasmin', 'Thé vert parfumé au jasmin (Chaud)', 3.00, 3, 'the.jpg', FALSE, TRUE),
('Jus de Litchi', 'Jus de fruit exotique 25cl', 3.00, 3, 'jus_litchi.jpg', FALSE, TRUE);
