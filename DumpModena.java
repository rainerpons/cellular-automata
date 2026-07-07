import javafx.application.Application;
import javafx.stage.Stage;
public class DumpModena extends Application {
    public void start(Stage s) {
        System.out.println(Application.getUserAgentStylesheet());
        System.exit(0);
    }
    public static void main(String[] args) { launch(args); }
}
