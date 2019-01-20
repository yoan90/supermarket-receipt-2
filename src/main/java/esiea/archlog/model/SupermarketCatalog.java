package esiea.archlog.model;

public interface SupermarketCatalog {
    void addProduct(Product product, double price);

    double getUnitPrice(Product product);

}
