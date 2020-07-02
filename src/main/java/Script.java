import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Script {

    private String username;
    private String password;

    public Script() {
        this.username = "";
        this.password = "";

    }

    public void run() throws IOException {

        WebDriver driver = SeleniumUtility.initWebDriver();
        driver.get("https://tieba.baidu.com");
        SeleniumUtility.waitPresence(driver, "tagName", "body");


        driver.findElement(By.cssSelector("#com_userbar > ul > li.u_login > div > a")).click();
        SeleniumUtility.waitVisible(driver, "id", "passport-login-pop");


        //find and save qr code to local
        String qrcodeURL = driver.findElement(By.className("tang-pass-qrcode-img")).getAttribute("src");
        while (qrcodeURL.contains("loading")) {
            qrcodeURL = driver.findElement(By.className("tang-pass-qrcode-img")).getAttribute("src");
        }
        URL qrcode = new URL(qrcodeURL);
        BufferedImage saveImage = ImageIO.read(qrcode);
        String path = System.getProperty("user.dir") + "/qrcode.png";
        ImageIO.write(saveImage, "png", new File(path));

        //login by username and password
        if (false) {
            driver.findElement(By.cssSelector("#TANGRAM__PSP_10__footerULoginBtn")).click();

            SeleniumUtility.waitClickable(driver, "id", "TANGRAM__PSP_10__submit");
            driver.findElement(By.id("TANGRAM__PSP_10__userName")).sendKeys(username);
            driver.findElement(By.id("TANGRAM__PSP_10__password")).sendKeys(password);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            driver.findElement(By.id("TANGRAM__PSP_10__submit")).click();
        }


        // security message or scan qr code, wait user to operate
        while (SeleniumUtility.checkExist(driver, "id", "passport-login-pop")
                || SeleniumUtility.checkExist(driver, "id", "TANGRAM__25__wrapper")
                || SeleniumUtility.checkExist(driver, "id", "TANGRAM__25__article")) {
        }
        SeleniumUtility.waitPresence(driver, "tagName", "body");

        //delete local qrcode file after log in for security
        File file = new File(path);
        file.delete();

        driver.get("http://tieba.baidu.com/home/main?un=leedsyu&id=b1286c656564737975e807&fr=index");
        SeleniumUtility.waitPresence(driver, "tagName", "body");
        driver.findElement(By.cssSelector("#ihome_nav_wrap > ul > li:nth-child(4) > div > p > a")).click();

        //switch to new tab
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(tabs.size() - 1));

        int numOfTieba = 0;
        boolean isLastPage = false;

        while (driver.findElement(By.cssSelector("#j_pagebar > div")).getText().contains("下一页") || isLastPage) {

            SeleniumUtility.waitPresence(driver, "tagName", "body");

            WebElement table = driver.findElement(By.cssSelector("#like_pagelet > div.forum_main > div.forum_table > table > tbody"));
            List<WebElement> listOfTieba = table.findElements(By.tagName("td"));
            for (int i = 0; i < listOfTieba.size(); i += 4) {

                WebElement tieba = listOfTieba.get(i).findElement(By.tagName("a"));
                System.out.print(tieba.getText());
                String url = tieba.getAttribute("href");

                SeleniumUtility.executeJavaScript(driver, "chrome", "window.open()");
                //switch to new tab
                tabs = new ArrayList<>(driver.getWindowHandles());
                driver.switchTo().window(tabs.get(tabs.size() - 1));
                driver.get(url);
                SeleniumUtility.waitPresence(driver, "tagName", "body");

                while (!driver.findElement(By.cssSelector("#signstar_wrapper > a")).getText().contains("连续")) {
                    SeleniumUtility.executeJavaScript(driver, "chrome", "document.querySelector(\"#signstar_wrapper > a\").click()");
                    SeleniumUtility.waitPresence(driver, "tagName", "body");

                    //captcha page
//                    while (SeleniumUtility.checkExist(driver, "id", "dialogJbody")) {
//                        String captchaURL = driver.findElement(By.cssSelector("#dialogJbody > div > div.tbui_captcha_container > div > div:nth-child(2) > span.j_captcha_content > span.tbui_captcha_img_wrap.j_captcha_img_wrapper > img")).getAttribute("src");
////                    while (qrcodeURL.contains("loading")) {
////                        qrcodeURL = driver.findElement(By.className("tang-pass-qrcode-img")).getAttribute("src");
////                    }
//                        URL captcha = new URL(captchaURL);
//                        BufferedImage saveCaptcha = ImageIO.read(captcha);
//                        String captchaPath = System.getProperty("user.dir") + "/captcha.png";
//                        ImageIO.write(saveCaptcha, "png", new File(captchaPath));
//                    }

                }
                System.out.println(" -- signed");
                numOfTieba++;

                driver.close();
                tabs = new ArrayList<>(driver.getWindowHandles());
                driver.switchTo().window(tabs.get(tabs.size() - 1));
            }

            List<WebElement> buttonTabs = driver.findElement(By.cssSelector("#j_pagebar > div")).findElements(By.tagName("a"));
            for (WebElement buttonTab : buttonTabs) {
                if (buttonTab.getText().contains("下一页")) {
                    buttonTab.click();
                    break;
                }
            }

            if (!driver.findElement(By.cssSelector("#j_pagebar > div")).getText().contains("下一页")) {
                isLastPage = !isLastPage;
            }
        }
        driver.quit();
        System.out.println("finished all " + numOfTieba + " tieba sign in");
    }
}

