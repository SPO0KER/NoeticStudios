package com.noetic.view;

import com.noetic.data.DataStore;
import com.noetic.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

public class ShopView {

    private ScrollPane root;
    private DataStore store = DataStore.getInstance();
    private MainView mainView;
    private String currentCategory = "Todos";
    private FlowPane productsGrid;

    private static final String[] CATEGORIES = {"Todos", "Camisetas", "Gorras", "Pantalones", "Jeans", "Buzos"};

    public ShopView(MainView mainView) {
        this.mainView = mainView;
        root = new ScrollPane();
        root.setFitToWidth(true);
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.getStyleClass().add("content-scroll");
        buildUI();
    }

    private void buildUI() {
        VBox container = new VBox(0);
        container.getStyleClass().add("shop-container");

         
        VBox hero = new VBox(8);
        hero.getStyleClass().add("shop-hero");
        hero.setPadding(new Insets(50, 60, 40, 60));

        Label heroTitle = new Label("MODA STREETWEAR");
        heroTitle.getStyleClass().add("hero-title");

        Label heroSubtitle = new Label("A UNIQUE IDENTITY WITH OUR GARMENTS");
        heroSubtitle.getStyleClass().add("hero-subtitle");

        hero.getChildren().addAll(heroTitle, heroSubtitle);

        
        VBox shopSection = new VBox(24);
        shopSection.getStyleClass().add("shop-section");
        shopSection.setPadding(new Insets(30, 40, 40, 40));

       
        HBox filterBar = new HBox(8);
        filterBar.getStyleClass().add("filter-bar");
        filterBar.setPadding(new Insets(16, 20, 16, 20));

        for (String cat : CATEGORIES) {
            Button catBtn = new Button(cat);
            catBtn.getStyleClass().add("filter-btn");
            if (cat.equals(currentCategory)) catBtn.getStyleClass().add("filter-btn-active");
            catBtn.setOnAction(e -> {
                currentCategory = cat;
                refreshProducts();
                  
                filterBar.getChildren().forEach(node -> {
                    if (node instanceof Button) {
                        node.getStyleClass().remove("filter-btn-active");
                    }
                });
                catBtn.getStyleClass().add("filter-btn-active");
            });
            filterBar.getChildren().add(catBtn);
        }

         
        productsGrid = new FlowPane();
        productsGrid.setHgap(20);
        productsGrid.setVgap(20);
        productsGrid.setPadding(new Insets(0));

        shopSection.getChildren().addAll(filterBar, productsGrid);
        container.getChildren().addAll(hero, shopSection);
        root.setContent(container);

        refreshProducts();
    }

    private void refreshProducts() {
        productsGrid.getChildren().clear();
        List<Product> products = store.getProductsByCategory(currentCategory);

        for (Product product : products) {
            productsGrid.getChildren().add(createProductCard(product));
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(0);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(260);

          
        StackPane imageArea = new StackPane();
        imageArea.getStyleClass().add("product-image-area");
        imageArea.setPrefHeight(220);

        VBox imgPlaceholder = new VBox();
        imgPlaceholder.setAlignment(Pos.CENTER);
        imgPlaceholder.getStyleClass().add("product-image-placeholder");
        Label imgIcon = new Label(getCategoryEmoji(product.getCategory()));
        imgIcon.setStyle("-fx-font-size: 60px;");
        imgPlaceholder.getChildren().add(imgIcon);
   
        Button wishBtn = new Button(store.isInWishlist(product) ? "♥" : "♡");
        wishBtn.getStyleClass().add(store.isInWishlist(product) ? "wish-btn-active" : "wish-btn");
        wishBtn.setOnAction(e -> {
            store.toggleWishlist(product);
            wishBtn.setText(store.isInWishlist(product) ? "♥" : "♡");
            wishBtn.getStyleClass().remove("wish-btn");
            wishBtn.getStyleClass().remove("wish-btn-active");
            wishBtn.getStyleClass().add(store.isInWishlist(product) ? "wish-btn-active" : "wish-btn");
            mainView.updateBadges();
        });

        imageArea.getChildren().addAll(imgPlaceholder, wishBtn);
        StackPane.setAlignment(wishBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(wishBtn, new Insets(10, 10, 0, 0));
   
        VBox cardBody = new VBox(6);
        cardBody.getStyleClass().add("product-card-body");
        cardBody.setPadding(new Insets(16, 16, 16, 16));

        Label catLabel = new Label(product.getCategory());
        catLabel.getStyleClass().add("product-category");

        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);

        Label descLabel = new Label(product.getDescription());
        descLabel.getStyleClass().add("product-desc");
        descLabel.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox priceRow = new HBox();
        priceRow.setAlignment(Pos.CENTER_LEFT);
        priceRow.setSpacing(12);

        VBox priceBox = new VBox(2);
        Label priceLabel = new Label(formatPrice(product.getPrice()));
        priceLabel.getStyleClass().add("product-price");
        Label stockLabel = new Label("Stock: " + product.getStock());
        stockLabel.getStyleClass().add("product-stock");
        priceBox.getChildren().addAll(priceLabel, stockLabel);

        Region priceSpacer = new Region();
        HBox.setHgrow(priceSpacer, Priority.ALWAYS);

        Button addBtn = new Button("🛒 Agregar");
        addBtn.getStyleClass().add("add-btn");
        addBtn.setDisable(product.getStock() == 0);
        addBtn.setOnAction(e -> {
            store.addToCart(product);
            mainView.updateBadges();
            mainView.showToast("Producto agregado al carrito");
        });

        priceRow.getChildren().addAll(priceBox, priceSpacer, addBtn);
        cardBody.getChildren().addAll(catLabel, nameLabel, descLabel, spacer, priceRow);

        card.getChildren().addAll(imageArea, cardBody);
        VBox.setVgrow(cardBody, Priority.ALWAYS);

        return card;
    }

    private String getCategoryEmoji(String category) {
        switch (category) {
            case "Camisetas": return "👕";
            case "Gorras": return "🧢";
            case "Pantalones": return "👖";
            case "Jeans": return "👖";
            case "Buzos": return "🧥";
            default: return "🛍";
        }
    }

    private String formatPrice(double price) {
        return String.format("$ %,.0f", price).replace(",", ".");
    }

    public ScrollPane getRoot() { return root; }
}
