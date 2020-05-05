import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ErrorPage {

    private static String errorPathString = "error.html";
    private static String errorHtmlURLTemplate = "{url}";
    private static String errorHtmlString = "";

    public static void load() throws IOException {
        errorHtmlString = new String(Files.readAllBytes(Paths.get(errorPathString)));
    }

    public static String getHtmlString(String url) {
        return errorHtmlString.replace(errorHtmlURLTemplate, url);
    }

    public static void setPath(String path){
        errorPathString = path;
    }

}
