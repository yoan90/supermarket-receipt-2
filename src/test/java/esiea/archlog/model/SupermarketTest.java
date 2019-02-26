package esiea.archlog.model;

import static org.assertj.core.api.Assertions.assertThat;
import esiea.archlog.ReceiptPrinter;
import org.junit.jupiter.api.Test;

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
        Product shampooing = new Product("shampooing", ProductUnit.Each);
        catalog.addProduct(shampooing, 5);
        Product croissants = new Product("croissants", ProductUnit.Each);
        catalog.addProduct(croissants, 0.8);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);
        cart.addItemQuantity(toothbrush, 20);
        cart.addItemQuantity(avocados, 3 );
        cart.addItemQuantity(shampooing, 2 );
        cart.addItemQuantity(croissants, 10);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TenPercentDiscount, toothbrush, 10.0);
        teller.addSpecialOffer(SpecialOfferType.ThreeForTwo, avocados, 0);
        teller.addSpecialOffer(SpecialOfferType.TwoForAmount, shampooing, 5);
        teller.addSpecialOffer(SpecialOfferType.FiveForAmount, croissants, 0.8);

        Offer offer = new Offer(SpecialOfferType.FiveForAmount, croissants, 0.8);

        Receipt receipt = teller.checksOutArticlesFrom(cart);
        assertThat(offer.getProduct()).isEqualTo(croissants);
        assertThat(teller.checksOutArticlesFrom(cart).getTotalPrice()).as("2,5 x 2kg of apples + 20 toothbrush at 1€ with 10 percent discount + ThreeForTwo avocados + 10 croissants for price of 2 = 5 + 20 - 2 + 5 + 1.6 + 4 € ").isEqualTo(33.6);
    }
    
    @Test
    public void testProductEqual() {
        Product toothbrush = new Product("toothbrush", ProductUnit.Each);
        Product pen = new Product("pen", ProductUnit.Each);
        Product toothbrush_2 = new Product("toothbrush_2", ProductUnit.Each);
        Product toothbrush_3 = new Product("toothbrush", ProductUnit.Kilo);
        Product toothbrush_4 = new Product("toothbrush", ProductUnit.Each);

        assertThat(toothbrush.equals(null)).isFalse();
        assertThat(toothbrush.equals(toothbrush)).isTrue();
        assertThat(toothbrush.equals("toothbrush")).isFalse();
        assertThat(toothbrush.equals(pen)).isFalse();
        assertThat(toothbrush.equals(toothbrush_2)).isFalse();
        assertThat(toothbrush.equals(toothbrush_3)).isFalse();
        assertThat(toothbrush.equals(toothbrush_4)).isTrue();
    }


    @Test
    public void testsSpecialOffers() {
        SupermarketCatalog catalog = new FakeCatalog();
        ShoppingCart cart = new ShoppingCart();
        Teller teller = new Teller(catalog);

        Product rose = new Product("rose", ProductUnit.Each);
        Product phone = new Product("phone", ProductUnit.Each);

        catalog.addProduct(rose, 1.0);
        teller.addSpecialOffer(SpecialOfferType.FiveForAmount, rose, 7.5);

        cart.addItemQuantity(rose, 3.0);
        assertThat(teller.checksOutArticlesFrom(cart).getTotalPrice()).isEqualTo(3.0);

        cart.addItemQuantity(rose, 2.0);
        assertThat(teller.checksOutArticlesFrom(cart).getTotalPrice()).isEqualTo(7.5);

        catalog.addProduct(phone, 1000.0);
        teller.addSpecialOffer(SpecialOfferType.TwoForAmount, phone, 4.0);

        cart.addItemQuantity(phone, 1.0);
        assertThat(teller.checksOutArticlesFrom(cart).getTotalPrice()).isEqualTo(1007.5);
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

        cart.addItem(toothpaste);

        receipt = teller.checksOutArticlesFrom(cart);

        assertThat(receipt.getTotalPrice()).isEqualTo(9.28);
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

        assertThat(cart.productQuantities.values().toString()).isNotNull();
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
        Product rice_kilo = new Product("rice", ProductUnit.Kilo);
        Product pen = new Product("pen", ProductUnit.Kilo);

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

        assertThat(receiptItem_1.equals(receiptItem_2)).isEqualTo(false);
        assertThat(receiptItem_1.equals(receiptItem_3)).isEqualTo(false);
        assertThat(receiptItem_1.equals(receiptItem_4)).isEqualTo(false);
        assertThat(receiptItem_1.equals(receiptItem_5)).isEqualTo(false);
    }


    @Test
    public void testReceiptItemEquals(){
        Product rice = new Product("rice", ProductUnit.Kilo);
        Product flour = new Product("flour", ProductUnit.Kilo);

        ReceiptItem receiptItem_ = new ReceiptItem(rice, 2.0, 2.0, 6.0);
        ReceiptItem receiptItem_1_ = new ReceiptItem(flour, 3.0, 1.0, 1.0);
        ReceiptItem receiptItem_2_ = new ReceiptItem(rice, 1.0, 2.0, 4.0);
        ReceiptItem receiptItem_3_ = new ReceiptItem(rice, 2.0, 3.0, 4.0);
        ReceiptItem receiptItem_4_ = new ReceiptItem(rice, 2.0, 2.0, 5.0);
        ReceiptItem receiptItem_5_ = new ReceiptItem(rice, 2.0, 2.0, 6.0);
        ReceiptItem receiptItem_6_ = new ReceiptItem(flour, 2.0, 2.0, 6.0);

        assertThat(receiptItem_).isNotEqualTo(null);
        assertThat(receiptItem_.equals(rice)).isEqualTo(false);
        assertThat(receiptItem_.equals(receiptItem_)).isEqualTo(true);
        assertThat(receiptItem_.equals(receiptItem_1_)).isEqualTo(false);
        assertThat(receiptItem_.equals(receiptItem_2_)).isEqualTo(false);
        assertThat(receiptItem_.equals(receiptItem_3_)).isEqualTo(false);
        assertThat(receiptItem_.equals(receiptItem_4_)).isEqualTo(false);
        assertThat(receiptItem_.equals(receiptItem_5_)).isNotEqualTo(false);
        assertThat(receiptItem_.equals(receiptItem_6_)).isEqualTo(false);
    }
}
