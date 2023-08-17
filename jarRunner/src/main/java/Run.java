import model.CmdObj;
import model.Credential;

public class Run {

    public static void main(String[] args) {


        CmdObj cmdObj = new CmdObj("/usr/local/share/java/opencv4/",
                "/var/www/rug-counter/models/load_model.onnx",
                "rugcounter.jar",
                "/var/www/rug-counter/inputImages/image2.jpeg",
                "/var/www/rug-counter/outputImages/result2.jpeg",
                "/usr/local/share/java/opencv4/");


        Credential credential = new Credential("165.22.3.2",
                "root",
                "GkRcGH6FeWdvyXQW5MDC",
                "https://rug-counter.boutiquerugs.com/",
                "/var/www/rug-counter/",
                "22");
        RunnerHelper runnerHelper = new RunnerHelper();

        try {
            runnerHelper.runner(credential, cmdObj);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
