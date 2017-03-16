package hello;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import views.MainFrame;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        MainFrame.getInstance().initWindow();
    }
}
