package wolf.mayizhuangyuan.init;

import com.github.jaemon.dinger.DingerSender;
import com.github.jaemon.dinger.core.entity.DingerRequest;
import com.github.jaemon.dinger.core.entity.enums.MessageSubType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableScheduling
@PropertySource("classpath:application.properties")
public class Answer {

    @Value("${spring.selenium.url}")
    private String url;
    @Value("${spring.answer.url}")
    private String answerUrl;

    @Autowired
    private DingerSender dingerSender;

    @Scheduled(cron = "${spring.scheduled.cron}")
    private void configureTasks() throws Exception {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("headless");
        chromeOptions.addArguments("disable-gpu");
        chromeOptions.addArguments("no-sandbox");
        //第一个参数：表示服务器的地址。第二个参数：表示预期的执行对象，其他的浏览器都可以以此类推`
        WebDriver driver = new RemoteWebDriver(new URL(url), chromeOptions);
        driver.get(answerUrl);
        String date = driver.findElement(By.xpath("//tbody/tr[2]/td[1]")).getText();
        LocalDateTime localDateTime = LocalDateTime.now();
        int day = localDateTime.getDayOfMonth();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("M月dd日");
        if (day < 10) {
            dateTimeFormatter = DateTimeFormatter.ofPattern("M月d日");
        }
        String now = dateTimeFormatter.format(localDateTime);
        if (date.equals(now)) {
            System.out.println("日期:" + date);
            WebElement webElement;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            for (int i = 2; i < 4; i++) {
                for (int j = 1; j < 4; j++) {
                    webElement = driver.findElement(By.xpath("//tbody/tr[" + i + "]/td[" + j + "]"));
                    stringBuilder.append(webElement.getText());
                    stringBuilder.append("\n");
                    System.out.println(webElement.getText());
                }
            }
            driver.quit();
            // 发送text类型消息
            dingerSender.send(
                    MessageSubType.TEXT,
                    DingerRequest.request(stringBuilder.toString())
            );
        } else {
            driver.quit();
            System.out.println("日期不一样,应该是未更新,等待一小时");
            //等待一小时
            Thread.sleep(1000 * 60 * 60);
            configureTasks();
        }
    }
}
