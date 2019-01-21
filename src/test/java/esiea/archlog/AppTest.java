package esiea.archlog;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;
/**
 * Unit test for simple App.
 */
public class AppTest
{

    @Test
    public void testApp()
    {
        /* Hello World Test */
        String testHelloWorld = "Hello World!";
        Assertions.assertThat(testHelloWorld).isEqualTo(App.hw);

        /* Default Test */
        Assertions.assertThat(true).isTrue();
    }
}
