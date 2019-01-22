package esiea.archlog.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

public class SupermarketTest {

    @Test
    public void testSomething() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.Each);
        catalog.addProduct(toothbrush, 0.99);
        Product apples = new Product("apples", ProductUnit.Kilo);
        catalog.addProduct(apples, 1.99);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TenPercentDiscount, toothbrush, 10.0);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertThat(receipt.getTotalPrice()).as("Price of 2,5kg of apples").isEqualTo(4.975);
    }
    
    @Test
    public void testProductEqual() {
        Product toothbrush = new Product("toothbrush", ProductUnit.Each);

        assertThat(toothbrush.equals(toothbrush)).as("Same Product").isTrue();

        Product apple = null;

        assertThat(toothbrush.equals(apple)).as("Product null").isFalse();

        ShoppingCart cart = new ShoppingCart();

        assertThat(toothbrush.equals(cart)).as("Product is the same type of class").isFalse();

        Product toothbrush2 = new Product("toothbrush", ProductUnit.Each);
        Product apple2 = new Product("apple", ProductUnit.Kilo);

        assertThat(toothbrush.equals(toothbrush2)).as("Same Unit and name").isTrue();
        assertThat(toothbrush.equals(apple2)).as("Same Unit and name").isFalse();
    }
   
    
    @Test
    public void testDiscountCar() {

        SupermarketCatalog catalog = new FakeCatalog();
        ShoppingCart cartCar = new ShoppingCart();
        Product car = new Product("car", ProductUnit.Each);
        catalog.addProduct(car, 10000);
        Discount discountCar = new Discount (car, "Discount of Car", 2000.0);
        cartCar.addItem(car);
        Teller teller = new Teller(catalog);
        Receipt receipt = teller.checksOutArticlesFrom(cartCar);
        receipt.addDiscount(discountCar);

        assertThat(receipt.getTotalPrice()).as("Price of discount car").isEqualTo(8000.0);

    }
}
