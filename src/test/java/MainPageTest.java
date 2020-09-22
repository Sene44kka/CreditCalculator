import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.junit.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainPageTest {

    private WebDriver driver;
    private MainPage mainPage;
    String time = "60";//Срок, на который берем кредит

    @Before
    public void setUp(){
        System.setProperty("webdriver.gecko.driver","C:\\Users\\Admin\\IdeaProjects\\CreditCalculator\\drivers\\geckodriver.exe");
        System.setProperty("webdriver.chrome.driver","C:\\Users\\Admin\\IdeaProjects\\CreditCalculator\\drivers\\chromedriver.exe");
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.get("https://fincalculator.ru/kreditnyj-kalkulyator");
        mainPage = new MainPage(driver);
    }
    @After
    public void tearDown(){
        driver.quit();
    }

    //Тест на расчет кредита без доп.функций с фиксированной ставкой, без комиссии, без досрочных погашений, не учитывая инфляцию
    @Test
    public void calculateTest(){
        mainPage.calculateLoan("100000",time,"10","10.10.2019");
        mainPage.chooseRootPeriodTypeDropdownText("1");
        mainPage.chooseFixRateTypeDropdownText();
        mainPage.choosePaymentTypeDropdownText("1");
        mainPage.choosePeriodTypeDropdownText("2");
        mainPage.clickCalculateButton();
        Assert.assertEquals("2 124,70", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[" + time + "]/td[3]")).getAttribute("innerHTML"));
    }
    @Test
    //Тест на расчет кредита с неправильными данными
    public void calculateInvalidDataTest(){
        mainPage.calculateLoan("-100000",time,"10","10.10.2019");
        ArrayList<WebElement> list = (ArrayList<WebElement>) driver.findElements(By.xpath("//span[@data-bind=\"text : 'Сумма платежа в месяц:'\"]"));
        Assert.assertFalse(list.size()>0);
    }
    @Test
    //Тест на расчет кредита с учетом инфляции
    public void calculateDiscountTest(){
        mainPage.calculateLoan("100000",time,"10","10.10.2019");
        mainPage.takeIntoAccountDiscount("10","2");
        mainPage.clickCalculateButton();
        Assert.assertEquals("1 319,27", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[" + time + "]/td[4]")).getAttribute("innerHTML"));
    }
    @Test
    //Тест на расчет кредита с учетом расширенных настроек с округлением платежей
    public void calculateAccountAdvancedSettingsWithPaymentRoundingTest(){
        mainPage.markAdditionalPaymentSettingsCheckBox();
        mainPage.takeIntoAccountAdvancedSettingsWithPaymentRounding("2");
        mainPage.calculateLoan("100000",time,"10","10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("2 124,70", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[" + time + "]/td[3]")).getAttribute("innerHTML"));
    }
    @Test
    //Расчитываем кредит с учетом расширенных настроек с выбором даты платежа
    public void calculateAccountAdvancedSettingsWithChoicePaymentDateTest(){
        mainPage.markAdditionalPaymentSettingsCheckBox();
        mainPage.takeIntoAccountAdvancedSettingsWithChoicePaymentDate("2");
        mainPage.calculateLoan("100000",time,"10","10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("30.09.2024", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[" + time + "]/td[2]")).getAttribute("innerHTML"));
    }
    @Test
    //Расчитываем кредит с учетом расширенных настроек с выбором переноса даты, если она выпала на выходной
    public void calculateAccountAdvancedSettingsWithTransferPaymentDateTest(){
        mainPage.markAdditionalPaymentSettingsCheckBox();
        mainPage.takeIntoAccountAdvancedSettingsWithTransferPaymentDate("1");
        mainPage.calculateLoan("100000",time,"10","10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("11.11.2019", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[1]/td[2]")).getAttribute("innerHTML"));
    }
    @Test
    //Расчитываем кредит с учетом расширенных настроек с выбором: "последний платеж не может превышать аннуитент"
    public void calculateAccountAdvancedSettingsWithLastPaymentTest(){
        mainPage.markAdditionalPaymentSettingsCheckBox();
        mainPage.takeIntoAccountAdvancedSettingsWithLastPayment();
        mainPage.calculateLoan("100000",time,"10","10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("4,51", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[" + time + "]/td[3]")).getAttribute("innerHTML"));
    }
    @Test
    //Расчитываем кредит с учетом расширенных настроек с выбором: "платить сначала только проценты"
    public void calculateAccountAdvancedSettingsPayOnlyPercentTest(){
        mainPage.markAdditionalPaymentSettingsCheckBox();
        mainPage.takeIntoAccountAdvancedSettingsPayOnlyPercent("6", "1");
        mainPage.calculateLoan("100000",time,"10","10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("2 299,91", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[" + time + "]/td[3]")).getAttribute("innerHTML"));
    }
    @Test
    //Тест на расчет кредита с учетом всех расширенных настроек вместе взятых И с учетом инфляции
    public void calculateAllAccountAdvancedSettingsTest(){
        mainPage.markAdditionalPaymentSettingsCheckBox();
        mainPage.takeIntoAccountAdvancedSettingsPayOnlyPercent("6", "1");
        mainPage.takeIntoAccountAdvancedSettingsWithLastPayment();
        mainPage.takeIntoAccountAdvancedSettingsWithTransferPaymentDate("1");
        mainPage.takeIntoAccountAdvancedSettingsWithChoicePaymentDate("2");
        mainPage.takeIntoAccountAdvancedSettingsWithPaymentRounding("2");
        mainPage.calculateLoan("100000",time,"10","10.10.2019");
        mainPage.takeIntoAccountDiscount("10","2");
        mainPage.clickCalculateButton();
        Assert.assertEquals("02.12.2019", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[1]/td[2]")).getAttribute("innerHTML"));
        Assert.assertEquals("1 397,26", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("13,08", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[" + time + "]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("8,12", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[" + time + "]/td[4]")).getAttribute("innerHTML"));
    }
    @Test
    //Тест на расчет кредита с условием, что ставка будет изменяться со временем
    public void calculateVariableRateTypeDropdownTest(){
        String[] rate = {"6,00", "5,00", "10,00"};
        String[] num = {"1", "2", "2"};
        String[] date = {"10.10.2020", "10.10.2021","10.10.2022"};
        mainPage.chooseVariableRateTypeDropdownText(3, rate, num, date);//кол-во изменений ставки, ставка, в месяц/год, дата
        mainPage.typeAmountTextField("100000");
        mainPage.typeTimeTextField("60");
        mainPage.typeDateTextField("10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("8 500,93", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 827,29", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[33]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 973,80", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[" + time + "]/td[3]")).getAttribute("innerHTML"));
    }
    @Test
    //Тест на расчет кредита с учетом всех расширенных настроек вместе взятых И с учетом инфляции. И с учетом, что ставка изменятся
    public void calculateAllAccountAdvancedSettingsWithVariableRateTypeDropdownTest(){
        mainPage.markAdditionalPaymentSettingsCheckBox();
        mainPage.takeIntoAccountAdvancedSettingsPayOnlyPercent("6", "1");
        mainPage.takeIntoAccountAdvancedSettingsWithLastPayment();
        mainPage.takeIntoAccountAdvancedSettingsWithTransferPaymentDate("1");
        mainPage.takeIntoAccountAdvancedSettingsWithChoicePaymentDate("2");
        mainPage.takeIntoAccountAdvancedSettingsWithPaymentRounding("2");
        String[] rate = {"10", "5", "3"};
        String[] num = {"1", "2", "1"};
        String[] date = {"10.10.2020", "10.10.2021","10.10.2022"};
        mainPage.chooseVariableRateTypeDropdownText(3, rate, num, date);//кол-во изменений ставки, ставка, в месяц/год, дата
        mainPage.typeAmountTextField("100000");
        mainPage.typeTimeTextField("60");
        mainPage.typeDateTextField("10.10.2019");
        mainPage.takeIntoAccountDiscount("10","2");
        mainPage.clickCalculateButton();
        Assert.assertEquals("02.12.2019", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[1]/td[2]")).getAttribute("innerHTML"));
        Assert.assertEquals("29 879,41", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("20,22", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[" + time + "]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("12,56", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[" + time + "]/td[4]")).getAttribute("innerHTML"));
        Assert.assertEquals("17 820,24", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[15]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 785,27", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[35]/td[3]")).getAttribute("innerHTML"));
    }
    @Test
    //Расчитать кредит с учетом комиссии. Единовременная коммиссия в рублях
    public void calculateTakeCommission1(){
        mainPage.takeCommission("Коммиссия1", "1","1000","1","5");
        mainPage.calculateLoan("100000",time,"10","10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("1 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[1]/td[7]")).getAttribute("innerHTML"));
    }
    @Test
    //Расчитать кредит с учетом комиссии. Ежемесячная коммиссия в процентах от суммы кредита
    public void calculateTakeCommission2(){
        mainPage.takeCommission("Коммиссия1", "2","10","2","5");
        mainPage.calculateLoan("100000",time,"10","10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][2]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][4]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][6]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][2]/td[7]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][4]/td[7]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][6]/td[7]")).getAttribute("innerHTML"));
    }
    @Test
    //Расчитать кредит с учетом комиссии. Ежегодная коммиссия в процентах от остатка от задолжности
    public void calculateTakeCommission3(){
        mainPage.calculateLoan("100000",time,"10","10.10.2019");
        mainPage.takeCommission("Коммиссия1", "3","10","3","5");
        mainPage.clickCalculateButton();
        Assert.assertEquals("8 520,67", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[13]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("6 742,01", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[26]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("4 777,85", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[39]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[13]/td[7]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[26]/td[7]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[39]/td[7]")).getAttribute("innerHTML"));
    }
    @Test
    //Расчитать кредит с учетом комиссии. Ежегодная коммиссия при каждом платеже в рублях
    public void calculateTakeCommission4() {
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.takeCommission("Коммиссия1", "4", "10", "1", "5");
        mainPage.clickCalculateButton();
        Assert.assertEquals("10,00", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("10,00", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][2]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("10,00", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][3]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1 (за ежемесячный платеж 11/2019)", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][1]/td[7]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1 (за ежемесячный платеж 12/2019)", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][2]/td[7]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1 (за ежемесячный платеж 01/2020)", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][3]/td[7]")).getAttribute("innerHTML"));
    }
    @Test
    //Расчитать кредит с учетом комиссии. Ежегодная коммиссия при каждом платеже в процентах от суммы платежа
    public void calculateTakeCommission5() {
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.takeCommission("Коммиссия1", "4", "10", "2", "5");
        mainPage.clickCalculateButton();
        Assert.assertEquals("212,47", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("212,47", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][2]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("212,47", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][3]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1 (за ежемесячный платеж 11/2019)", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][1]/td[7]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1 (за ежемесячный платеж 12/2019)", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][2]/td[7]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1 (за ежемесячный платеж 01/2020)", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][3]/td[7]")).getAttribute("innerHTML"));
    }
    @Test
    //Расчитать кредит с учетом комиссии. Ежегодная коммиссия при досрочном платеже в процентах от суммы платежа
    public void calculateTakeCommission6() {
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.takeCommission("Коммиссия1", "5", "10", "1", "5");
        mainPage.clickCalculateButton();
        Assert.assertEquals("2 124,70", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 124,70", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[3]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 124,70", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[5]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("Ежемесячный платеж за 11/2019", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[1]/td[7]")).getAttribute("innerHTML"));
        Assert.assertEquals("Ежемесячный платеж за 01/2020", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[3]/td[7]")).getAttribute("innerHTML"));
        Assert.assertEquals("Ежемесячный платеж за 03/2020", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[5]/td[7]")).getAttribute("innerHTML"));
    }
    @Test
    //Расчитать кредит с учетом комиссии. Ежегодная коммиссия при досрочном платеже в процентах от суммы платежа
    public void calculateTakeCommission7() {
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.takeCommission("Коммиссия1", "5", "10", "2", "5");
        mainPage.clickCalculateButton();
        Assert.assertEquals("2 124,70", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 124,70", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[3]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 124,70", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[5]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("Ежемесячный платеж за 11/2019", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[1]/td[7]")).getAttribute("innerHTML"));
        Assert.assertEquals("Ежемесячный платеж за 01/2020", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[3]/td[7]")).getAttribute("innerHTML"));
        Assert.assertEquals("Ежемесячный платеж за 03/2020", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[5]/td[7]")).getAttribute("innerHTML"));
    }
    @Test
    //Тест на расчет кредита с учетом всех расширенных настроек вместе взятых И с учетом инфляции. И с учетом, что ставка изменятся
    //И еще с коммиссией
    public void calculateAllAccountAdvancedSettingsWithVariableRateTypeDropdownTestWithCommission(){
        mainPage.markAdditionalPaymentSettingsCheckBox();
        mainPage.takeIntoAccountAdvancedSettingsPayOnlyPercent("6", "1");
        mainPage.takeIntoAccountAdvancedSettingsWithLastPayment();
        mainPage.takeIntoAccountAdvancedSettingsWithTransferPaymentDate("1");
        mainPage.takeIntoAccountAdvancedSettingsWithChoicePaymentDate("2");
        mainPage.takeIntoAccountAdvancedSettingsWithPaymentRounding("2");
        String[] rate = {"10", "5", "3"};
        String[] num = {"1", "2", "1"};
        String[] date = {"10.10.2020", "10.10.2021","10.10.2022"};
        mainPage.chooseVariableRateTypeDropdownText(3, rate, num, date);//кол-во изменений ставки, ставка, в месяц/год, дата
        mainPage.typeAmountTextField("100000");
        mainPage.typeTimeTextField("60");
        mainPage.typeDateTextField("10.10.2019");
        mainPage.takeIntoAccountDiscount("10","2");
        mainPage.takeCommission("Коммиссия1", "4", "10", "2", "5");
        mainPage.clickCalculateButton();
        Assert.assertEquals("02.12.2019", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][1]/td[2]")).getAttribute("innerHTML"));
        Assert.assertEquals("1 816,20", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][2]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("1 787,58", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][2]/td[4]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1 (за ежемесячный платеж 12/2019)", driver.findElement(By.xpath("//tbody/tr[@class=\"commission\"][2]/td[9]")).getAttribute("innerHTML"));
        Assert.assertEquals("99 208,89", driver.findElement(By.xpath("//tbody/tr[@class=\"payment\"][1]/td[8]")).getAttribute("innerHTML"));
        Assert.assertEquals("29 643,03", driver.findElement(By.xpath("//tbody/tr[@class=\"payment\"][1]/td[4]")).getAttribute("innerHTML"));
        Assert.assertEquals("29 879,41", driver.findElement(By.xpath("//tbody/tr[@class=\"payment\"][1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("29 879,41", driver.findElement(By.xpath("//tbody/tr[@class=\"payment\"][1]/td[6]")).getAttribute("innerHTML"));
    }
    @Test
    //Тест на расчет кредита с учетом единоразового досрочного платежа с уменьшением срока кредита
    public void calculateTakeEarlyRepayment1(){
        mainPage.takeEarlyRepayment("1","10000","10.10.2020","1");
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[13]/td[3]")).getAttribute("innerHTML"));
        Assert.assertTrue(driver.findElements(By.xpath("//tbody[@role=\"alert\"]/tr")).size()<Integer.parseInt(time));
    }
    @Test
    //Тест на расчет кредита с учетом досрочного платежа раз в месяц с уменьшением срока кредита
    public void calculateTakeEarlyRepayment2(){
        mainPage.takeEarlyRepayment("2","10000","10.10.2020","1");
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[13]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[15]/td[3]")).getAttribute("innerHTML"));
        Assert.assertTrue(driver.findElements(By.xpath("//tbody[@role=\"alert\"]/tr")).size()<Integer.parseInt(time));
    }
    @Test
    //Тест на расчет кредита с учетом досрочного платежа раз в 2 месяца с уменьшением срока кредита
    public void calculateTakeEarlyRepayment3(){
        mainPage.takeEarlyRepayment("3","10000","10.10.2020","1");
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[13]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[16]/td[3]")).getAttribute("innerHTML"));
        Assert.assertTrue(driver.findElements(By.xpath("//tbody[@role=\"alert\"]/tr")).size()<Integer.parseInt(time));
    }
    @Test
    //Тест на расчет кредита с учетом досрочного платежа раз в 3 месяца с уменьшением срока кредита
    public void calculateTakeEarlyRepayment4(){
        mainPage.takeEarlyRepayment("4","10000","10.10.2020","1");
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[13]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[17]/td[3]")).getAttribute("innerHTML"));
        Assert.assertTrue(driver.findElements(By.xpath("//tbody[@role=\"alert\"]/tr")).size()<Integer.parseInt(time));
    }
    @Test
    //Тест на расчет кредита с учетом досрочного платежа раз в 4 месяца с уменьшением срока кредита
    public void calculateTakeEarlyRepayment5(){
        mainPage.takeEarlyRepayment("5","10000","10.10.2020","1");
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[13]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[18]/td[3]")).getAttribute("innerHTML"));
        Assert.assertTrue(driver.findElements(By.xpath("//tbody[@role=\"alert\"]/tr")).size()<Integer.parseInt(time));
    }
    @Test
    //Тест на расчет кредита с учетом досрочного платежа раз в 6 месяца с уменьшением срока кредита
    public void calculateTakeEarlyRepayment6(){
        mainPage.takeEarlyRepayment("6","10000","10.10.2020","1");
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[13]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[20]/td[3]")).getAttribute("innerHTML"));
        Assert.assertTrue(driver.findElements(By.xpath("//tbody[@role=\"alert\"]/tr")).size()<Integer.parseInt(time));
    }
    @Test
    //Тест на расчет кредита с учетом досрочного платежа раз в 9 месяца с уменьшением срока кредита
    public void calculateTakeEarlyRepayment7(){
        mainPage.takeEarlyRepayment("7","10000","10.10.2020","1");
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[13]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[23]/td[3]")).getAttribute("innerHTML"));
        Assert.assertTrue(driver.findElements(By.xpath("//tbody[@role=\"alert\"]/tr")).size()<Integer.parseInt(time));
    }
    @Test
    //Тест на расчет кредита с учетом досрочного платежа раз в 12 месяца с уменьшением срока кредита
    public void calculateTakeEarlyRepayment8(){
        mainPage.takeEarlyRepayment("8","10000","10.10.2020","1");
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[13]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody[@role=\"alert\"]/tr[26]/td[3]")).getAttribute("innerHTML"));
        Assert.assertTrue(driver.findElements(By.xpath("//tbody[@role=\"alert\"]/tr")).size()<Integer.parseInt(time));
    }
    @Test
    //Тест на расчет кредита с учетом досрочного платежа раз в 1 месяц с уменьшением ежемесячного платежа
    public void calculateTakeEarlyRepayment9(){
        mainPage.takeEarlyRepayment("2","100","10.10.2020","2");
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("100,00", driver.findElement(By.xpath("//tbody/tr[@class=\"additional-repayment\"][1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 122,35", driver.findElement(By.xpath("//tbody/tr[@class=\"payment\"][13]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 120,07", driver.findElement(By.xpath("//tbody/tr[@class=\"payment\"][14]/td[3]")).getAttribute("innerHTML"));
        Assert.assertTrue(driver.findElements(By.xpath("//tbody/tr[@class=\"payment\"]")).size() == Integer.parseInt(time));
    }
    @Test
    //Тест на расчет кредита с учетом досрочного платежа раз в 1 месяц с уменьшением ежемесячного платежа с неверной суммой платежа
    public void calculateTakeEarlyRepayment10(){
        mainPage.takeEarlyRepayment("2","-100","10.10.2020","2");
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("0,00", driver.findElement(By.xpath("//tbody/tr[@class=\"additional-repayment\"][1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 124,70", driver.findElement(By.xpath("//tbody/tr[@class=\"payment\"][13]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 124,70", driver.findElement(By.xpath("//tbody/tr[@class=\"payment\"][14]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("Суммы досрочного погашение не достаточно для уплаты процентов", driver.findElement(By.xpath("//tbody/tr[@class=\"additional-repayment\"][1]/td[7]")).getAttribute("innerHTML"));
        Assert.assertTrue(driver.findElements(By.xpath("//tbody/tr[@class=\"payment\"]")).size() == Integer.parseInt(time)+1);
    }
    @Test
    //Тест на расчет кредита с учетом досрочного платежа раз в 1 месяц с уменьшением ежемесячного платежа
    //при условии,что след. ежемесячный платеж - % + погашение
    public void calculateTakeEarlyRepayment11(){
        mainPage.takeEarlyRepayment("2","100","10.10.2020","2");
        driver.findElement(By.xpath("//label[@for=\"loanAdvancedRepayments0.payOnDayOfLoanPayment\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanAdvancedRepayments0.firstPaymentPolicy\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanAdvancedRepayments0.firstPaymentPolicy\"]/option[1]")).click();
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("100,00", driver.findElement(By.xpath("//tbody/tr[@class=\"additional-repayment\"][1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 122,35", driver.findElement(By.xpath("//tbody/tr[@class=\"payment\"][13]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 120,07", driver.findElement(By.xpath("//tbody/tr[@class=\"payment\"][14]/td[3]")).getAttribute("innerHTML"));
        Assert.assertTrue(driver.findElements(By.xpath("//tbody/tr[@class=\"payment\"]")).size() == Integer.parseInt(time));
    }
    @Test
    //Тест на расчет кредита с учетом досрочного платежа раз в 1 месяц с уменьшением ежемесячного платежа
    //при условии,что пропустить след. ежемесячный платеж
    public void calculateTakeEarlyRepayment12(){
        mainPage.takeEarlyRepayment("2","100","10.10.2020","2");
        driver.findElement(By.xpath("//label[@for=\"loanAdvancedRepayments0.payOnDayOfLoanPayment\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanAdvancedRepayments0.firstPaymentPolicy\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanAdvancedRepayments0.firstPaymentPolicy\"]/option[2]")).click();
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("100,00", driver.findElement(By.xpath("//tbody/tr[@class=\"additional-repayment\"][1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("708,77", driver.findElement(By.xpath("//tbody/tr[@class=\"payment\"][13]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("2 156,54", driver.findElement(By.xpath("//tbody/tr[@class=\"payment\"][14]/td[3]")).getAttribute("innerHTML"));
        Assert.assertTrue(driver.findElements(By.xpath("//tbody/tr[@class=\"payment\"]")).size() == Integer.parseInt(time));
    }
    @Test
    //Тест на расчет кредита с учетом досрочного платежа раз в 1 месяц с уменьшением ежемесячного платежа
    //при условии,что след. ежемесячный платеж - только %
    public void calculateTakeEarlyRepayment13(){
        mainPage.takeEarlyRepayment("2","10000","10.10.2020","2");
        driver.findElement(By.xpath("//label[@for=\"loanAdvancedRepayments0.payOnDayOfLoanPayment\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanAdvancedRepayments0.firstPaymentPolicy\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanAdvancedRepayments0.firstPaymentPolicy\"]/option[3]")).click();
        mainPage.calculateLoan("100000", time, "10", "10.10.2019");
        mainPage.clickCalculateButton();
        Assert.assertEquals("10 000,00", driver.findElement(By.xpath("//tbody/tr[@class=\"additional-repayment odd\"][1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("624,92", driver.findElement(By.xpath("//tbody/tr[@class=\"payment even\"][7]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("1 645,66", driver.findElement(By.xpath("//tbody/tr[@class=\"payment even\"][8]/td[3]")).getAttribute("innerHTML"));
        Assert.assertTrue(driver.findElements(By.xpath("//tbody/tr[@class=\"payment even\"]")).size() == Integer.parseInt(time)-46);
    }
    @Test
    //Тест на расчет кредита с учетом всех расширенных настроек вместе взятых И с учетом инфляции. И с учетом, что ставка изменятся
    //И еще с коммиссией. Так же с учетом досрочного платежа каждый месяц и при условии, что след ежемес. платеж - только %
    public void calculateAllAccountAdvancedSettingsVariableRateTypeDropdownTestCommissionEarlyRepayment(){
        mainPage.markAdditionalPaymentSettingsCheckBox();
        mainPage.takeIntoAccountAdvancedSettingsPayOnlyPercent("6", "1");
        mainPage.takeIntoAccountAdvancedSettingsWithLastPayment();
        mainPage.takeIntoAccountAdvancedSettingsWithTransferPaymentDate("1");
        mainPage.takeIntoAccountAdvancedSettingsWithChoicePaymentDate("2");
        mainPage.takeIntoAccountAdvancedSettingsWithPaymentRounding("2");
        String[] rate = {"10", "5", "3"};
        String[] num = {"1", "2", "1"};
        String[] date = {"10.10.2020", "10.10.2021","10.10.2022"};
        mainPage.chooseVariableRateTypeDropdownText(3, rate, num, date);//кол-во изменений ставки, ставка, в месяц/год, дата
        mainPage.typeAmountTextField("100000");
        mainPage.typeTimeTextField("60");
        mainPage.typeDateTextField("10.10.2019");
        mainPage.takeIntoAccountDiscount("10","2");
        mainPage.takeCommission("Коммиссия1", "4", "10", "2", "5");
        mainPage.takeEarlyRepayment("2","10000","10.10.2020","2");
        driver.findElement(By.xpath("//label[@for=\"loanAdvancedRepayments0.payOnDayOfLoanPayment\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanAdvancedRepayments0.firstPaymentPolicy\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanAdvancedRepayments0.firstPaymentPolicy\"]/option[3]")).click();
        mainPage.clickCalculateButton();
        Assert.assertEquals("02.12.2019", driver.findElement(By.xpath("//tbody/tr[@class=\"commission odd\"][1]/td[2]")).getAttribute("innerHTML"));
        Assert.assertEquals("1 816,20", driver.findElement(By.xpath("//tbody/tr[@class=\"commission odd\"][2]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("1 787,58", driver.findElement(By.xpath("//tbody/tr[@class=\"commission odd\"][2]/td[4]")).getAttribute("innerHTML"));
        Assert.assertEquals("Коммиссия1 (за ежемесячный платеж 12/2019)", driver.findElement(By.xpath("//tbody/tr[@class=\"commission odd\"][2]/td[9]")).getAttribute("innerHTML"));
        Assert.assertEquals("99 208,89", driver.findElement(By.xpath("//tbody/tr[@class=\"payment even\"][1]/td[8]")).getAttribute("innerHTML"));
        Assert.assertEquals("29 643,03", driver.findElement(By.xpath("//tbody/tr[@class=\"payment even\"][1]/td[4]")).getAttribute("innerHTML"));
        Assert.assertEquals("29 879,41", driver.findElement(By.xpath("//tbody/tr[@class=\"payment even\"][1]/td[3]")).getAttribute("innerHTML"));
        Assert.assertEquals("29 879,41", driver.findElement(By.xpath("//tbody/tr[@class=\"payment even\"][1]/td[6]")).getAttribute("innerHTML"));
        Assert.assertEquals("8 264,46", driver.findElement(By.xpath("//tbody/tr[24]/td[@class=\"inflation decimal \"][1]")).getAttribute("innerHTML"));
    }
}
