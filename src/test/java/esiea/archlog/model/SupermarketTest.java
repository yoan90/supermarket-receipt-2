package esiea.archlog.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

import esiea.archlog.ReceiptPrinter;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class SupermarketTest {

    @Test
    public void testSomething() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.Each);
        catalog.addProduct(toothbrush, 1);
        Product apples = new Product("apples", ProductUnit.Kilo);
        catalog.addProduct(apples, 2);
        Product avocados = new Product("avocados", ProductUnit.Each);
        catalog.addProduct(avocados, 2);
        Product shampoing = new Product("shampoing", ProductUnit.Each);
        catalog.addProduct(shampoing, 5);
        Product croissants = new Product("croissants", ProductUnit.Each);
        catalog.addProduct(croissants, 0.8);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);
        cart.addItemQuantity(toothbrush, 20);
        cart.addItemQuantity(avocados, 3 );
        cart.addItemQuantity(shampoing, 2 );
        cart.addItemQuantity(croissants, 10);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TenPercentDiscount, toothbrush, 10.0);
        teller.addSpecialOffer(SpecialOfferType.ThreeForTwo, avocados, 0);
        teller.addSpecialOffer(SpecialOfferType.TwoForAmount, shampoing, 5);
        teller.addSpecialOffer(SpecialOfferType.FiveForAmount, croissants, 0.8);

        Offer offer = new Offer(SpecialOfferType.FiveForAmount, croissants, 0.8);

        Receipt receipt = teller.checksOutArticlesFrom(cart);
        assertThat(offer.getProduct()).isEqualTo(croissants);
        assertThat(teller.checksOutArticlesFrom(cart).getTotalPrice()).as("2,5 x 2kg of apples + 20 toothbrush at 1€ with 10 percent discount + ThreeForTwo avocados + 10 croissants for price of 2 = 5 + 20 - 2 + 5 + 1.6 + 4 € ").isEqualTo(33.6);
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
        assertThat(toothbrush.getUnit()).isEqualTo(ProductUnit.Each);
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
        assertThat(discountCar.getDescription()).isEqualTo("Discount of Car");
        assertThat(discountCar.getDiscountAmount()).isEqualTo(2000.0);
        assertThat(discountCar.getProduct()).isEqualTo(car);
    }


    @Test
    public void testTwoForAmount() {
        SupermarketCatalog catalog = new FakeCatalog();
        ShoppingCart cart = new ShoppingCart();
        Teller teller = new Teller(catalog);

        Product toothbrush = new Product("toothbrush", ProductUnit.Each);

        catalog.addProduct(toothbrush, 0.99);
        cart.addItemQuantity(toothbrush, 2);
        teller.addSpecialOffer(SpecialOfferType.TwoForAmount, toothbrush, 0.99);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertThat(receipt.getTotalPrice()).as("two toothbrushes for :").isEqualTo(0.99);
    }


    @Test
    public void testThreeForTwo() {
        SupermarketCatalog catalog = new FakeCatalog();
        ShoppingCart cart = new ShoppingCart();
        Teller teller = new Teller(catalog);

        Product toothpaste  = new Product("toothpaste tube", ProductUnit.Each);

        catalog.addProduct(toothpaste, 10);
        cart.addItemQuantity(toothpaste,2);
        teller.addSpecialOffer(SpecialOfferType.ThreeForTwo,toothpaste,2);

        Receipt receipt = teller.checksOutArticlesFrom(cart);
        assertThat(receipt.getTotalPrice()).as("two toothpaste tubes for :").isEqualTo(20);

        cart.addItemQuantity(toothpaste,1);

        receipt = teller.checksOutArticlesFrom(cart);
        assertThat(receipt.getTotalPrice()).as("three toothpaste tubes for :").isEqualTo(20);
    }


    @Test
    public void testFiveForAmount() {
        SupermarketCatalog catalog = new FakeCatalog();
        ShoppingCart cart = new ShoppingCart();
        Teller teller = new Teller(catalog);

        Product toothpaste  = new Product("toothpaste tube", ProductUnit.Each);

        catalog.addProduct(toothpaste , 1.79);
        cart.addItemQuantity(toothpaste , 5);
        teller.addSpecialOffer(SpecialOfferType.FiveForAmount, toothpaste , 7.49);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertThat(receipt.getTotalPrice()).as("five toothpaste tubes for :").isEqualTo(7.49);
    }

    
    @Test
    public void testShoppingCart() {
        ShoppingCart cart = new ShoppingCart();
        Product apples = new Product("apples", ProductUnit.Kilo);
        Product bananas = new Product("bananas", ProductUnit.Each);


        /* Test d'ajout d'un nouvel item */
        cart.addItemQuantity(apples, 3);

        /* Test d'ajout de même item */
        cart.addItemQuantity(apples, 3);

        /* Test d'ajout d'un nouvel item */
        cart.addItem(bananas);

        assertThat(cart.productQuantities.values().toString()).as("product(s) in the shopping cart : ").isEqualTo("[1.0, 6.0]");

    }
    
    
    @Test
    public void testReceiptPrinter(){
        SupermarketCatalog catalog = new FakeCatalog();
        ShoppingCart cart = new ShoppingCart();
        Teller teller = new Teller(catalog);
        ReceiptPrinter printer = new ReceiptPrinter();

        Product rice = new Product("rice", ProductUnit.Kilo);
        catalog.addProduct(rice, 3);
        cart.addItemQuantity(rice,10);

        Product pen = new Product("pen", ProductUnit.Each);
        catalog.addProduct(pen, 2.5);
        cart.addItemQuantity(pen,1);

        Product apples = new Product("apples", ProductUnit.Kilo);
        catalog.addProduct(apples, 2);
        cart.addItemQuantity(apples,3);

        teller.addSpecialOffer(SpecialOfferType.ThreeForTwo, apples, 1);
        Receipt receipt = teller.checksOutArticlesFrom(cart);
        assertThat(printer.printReceipt(receipt)).isNotBlank();

        String string_actual = printer.printReceipt(receipt);
        String string_expected = "rice"+"                               "+"30.00"+"\n"+"  "+"3.00"+" * "+"10.000"+"\n"+"pen"+"                                 "+"2.50"+"\n"+"apples"+"                              "+"6.00"+"\n"+"  2.00 * 3.000"+"\n"+"3 for 2"+"("+"apples"+")"+"                    "+"-"+"2.00"+"\n"+"\n"+"Total:"+"                             "+"36.50";

        assertThat(string_actual).isEqualTo(string_expected);
    }


    @Test
    public void testReceiptItem(){
        Product rice_kilo = new Product("rice k", ProductUnit.Kilo);
        Product rice_kilo_1 = new Product("rice k", ProductUnit.Kilo);
        Product rice_each = new Product("rice e", ProductUnit.Each);
        Product pen = new Product("pen", ProductUnit.Each);

        assertThat(rice_kilo.equals(rice_each)).isFalse();
        assertThat(rice_kilo.equals(rice_kilo_1)).isTrue();

        ReceiptItem receiptItem_1_1 = new ReceiptItem(rice_kilo,10,3, 30.0);
        ReceiptItem receiptItem_1 = new ReceiptItem(rice_kilo,10,3, 30.0);
        ReceiptItem receiptItem_2 = new ReceiptItem(rice_kilo,10,2, 30.0);
        ReceiptItem receiptItem_3 = new ReceiptItem(rice_kilo,10,3, 20.0);
        ReceiptItem receiptItem_4 = new ReceiptItem(rice_kilo,5,3, 30.0);
        ReceiptItem receiptItem_5 = new ReceiptItem(pen,1,1.5, 1.5);

        assertThat(receiptItem_1.getPrice()).isEqualTo(3.0);
        assertThat(receiptItem_1.getTotalPrice()).isEqualTo(30.0);
        assertThat(receiptItem_1.getProduct()).isEqualTo(rice_kilo);
        assertThat(receiptItem_1.getQuantity()).isEqualTo(10.0);

        assertThat(receiptItem_1.hashCode()).as("hash").isEqualTo(receiptItem_1.hashCode());
        assertThat(receiptItem_1.hashCode()).as("hash").isNotEqualTo(receiptItem_2.hashCode());


        assertThat(receiptItem_1.equals(null)).isFalse();
        assertThat(receiptItem_1.equals(receiptItem_1)).isEqualTo(true);
        assertThat(receiptItem_1.equals(receiptItem_1)).isNotEqualTo(null);
        assertThat(receiptItem_1.equals(receiptItem_1_1)).isEqualTo(true);
        assertThat(receiptItem_1.equals(receiptItem_2)).isEqualTo(false);
        assertThat(receiptItem_1.equals(receiptItem_3)).isEqualTo(false);
        assertThat(receiptItem_1.equals(receiptItem_4)).isEqualTo(false);
        assertThat(receiptItem_1.equals(receiptItem_5)).isEqualTo(false);
    }
}
