package automationTask;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        MailSender mailSender = new MailSender("danielpitimson096@gmail.com",
                "LHGBNCTY171",
                "pt21071996@gmail.com");
        ChromiumNotifier notifier = new ChromiumNotifier();
        notifier.run(new ArrayList<>(Arrays.asList("Windows", "Linux")), mailSender);
    }
}
