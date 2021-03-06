import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.*;

public class AcceptanceTest {

    /**
     * in this example we just check if the welcome page is being shown
     */
    @Test
    public void test() {
        running(testServer(9000, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:9000");
            assertThat(browser.pageSource(), containsString("Add Person"));
        });
    }

}
