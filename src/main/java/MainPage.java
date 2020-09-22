import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;

public class MainPage {
    private WebDriver driver;

    public MainPage(WebDriver driver) {
        this.driver = driver;
    }

    private final By calculateButton = By.xpath("//button[@class=\"btn btn-block btn-primary\"]");
    private final By amountTextField = By.xpath("//input[@id=\"loanParameters.amount\"]");//Ввести сумму кредита
    private final By timeTextField = By.xpath("//input[@id=\"loanParameters.root_periodValue\"]");//Ввести срок кредита
    private final By rateTextField = By.xpath("//input[@id=\"loanParameters.rate\"]");//Ввести ставку по кредиту
    private final By dateTextField = By.xpath("//input[@id=\"loanParameters.date\"]");//Ввести дату получения кредита
    private final By rootPeriodTypeDropdownText = By.xpath("//select[@id=\"loanParameters.root_periodType\"]");//ВЫбрать продолжительность срока. В месяцах или годах
    private final By rateTypeDropdownText = By.xpath("//select[@id=\"loanParameters.rateType\"]");//Выбрать тип ставки
    private final By periodTypeDropdownText = By.xpath("//select[@id=\"loanParameters.periodType\"]");//Тип периода начисления процентов
    private final By paymentTypeDropdownText = By.xpath("//select[@id=\"loanParameters.paymentType\"]");//Тип платежей
    private final By additionalPaymentSettingsCheckBox = By.xpath("//input[@id=\"loanParameters.root_hasAdditionalPaymentSettings\"]");//Расширенные настройки
    private final By isDiscountedCheckBox = By.xpath("//input[@id=\"inflationParameters.isDiscounted\"]");//Учитывать инфляцию

    //Кликните на кнопку: "Расчитать"
    public void clickCalculateButton(){
        driver.findElement(calculateButton).click();
    }
    //Введите сумму кредита
    public void typeAmountTextField(String amount){
        driver.findElement(amountTextField).clear();
        driver.findElement(amountTextField).sendKeys(amount);
    }
    //Введите срок, на который берете кредит
    public void typeTimeTextField(String time){
        driver.findElement(timeTextField).clear();
        driver.findElement(timeTextField).sendKeys(time);
    }
    //Введите процентную ставку, по которой берете кредит
    public void typeRateTextField(String rate){
        driver.findElement(rateTextField).clear();
        driver.findElement(rateTextField).clear();
        driver.findElement(rateTextField).sendKeys(rate);
    }
    //Введите дату получения кредита в формате: "DD.MM.yyyy"
    public void typeDateTextField(String date){
        driver.findElement(dateTextField).clear();
        driver.findElement(dateTextField).sendKeys(date);
        driver.findElement(dateTextField).click();
    }
    //Выберите тип срока, на который берете кредит. 1-месяцы. 2-годы
    public void chooseRootPeriodTypeDropdownText(String num){
        driver.findElement(rootPeriodTypeDropdownText).click();
        driver.findElement(By.xpath("//select[@id=\"loanParameters.root_periodType\"]/option[" + num + "]")).click();
    }
    //Выберите тип ставки: 1-фиксированная
    public void chooseFixRateTypeDropdownText(){
        driver.findElement(rateTypeDropdownText).click();
        driver.findElement(By.xpath("//select[@id=\"loanParameters.rateType\"]/option[1]")).click();
    }
    //Выберите тип платежей: 1-Аннуитетные. 2-Дифференцированные
    public void choosePaymentTypeDropdownText(String num) {
        driver.findElement(paymentTypeDropdownText).click();
        driver.findElement(By.xpath("//select[@id=\"loanParameters.paymentType\"]/option[" + num + "]")).click();
    }
    //Выберите тип периода начисления процентов: 1-каждый месяц. 2-каждый год
    public void choosePeriodTypeDropdownText(String num){
        driver.findElement(periodTypeDropdownText).click();
        driver.findElement(By.xpath("//select[@id=\"loanParameters.periodType\"]/option[" + num + "]")).click();
    }
    //Отметить поле: Расширенные настройки
    public void markAdditionalPaymentSettingsCheckBox(){
        driver.findElement(additionalPaymentSettingsCheckBox).click();
    }
    //Задать поля для расчета кредита без доп.функций с фиксированной ставкой, без комиссии, без досрочных погашений, не учитывая инфляцию
    public void calculateLoan(String amount, String time, String rate, String date){
        typeAmountTextField(amount);
        typeTimeTextField(time);
        typeRateTextField(rate);
        typeDateTextField(date);
    }
    //Учесть инфляцию. rate - ставка. num: 1 - учитывать инфляцию каждый месяц. 2 - каждый год
    public void takeIntoAccountDiscount(String rate, String num){
        driver.findElement(isDiscountedCheckBox).click();
        driver.findElement(By.xpath("//input[@id=\"inflationParameters.rate\"]")).sendKeys(rate);
        driver.findElement(By.xpath("//select[@id=\"inflationParameters.periodType\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"inflationParameters.periodType\"]/option[" + num + "]")).click();
    }
    //Учитываем расширенные настройки. Округляем платеж: 1 - до рублей. 2 - до копеек
    public void takeIntoAccountAdvancedSettingsWithPaymentRounding(String num){
        driver.findElement(By.xpath("//select[@id=\"loanParameters.paymentRoundType\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanParameters.paymentRoundType\"]/option[" + num + "]")).click();
    }
    //Учитываем расширенные настройки. Выбор даты платежа: 1 - в день выдачи кредита. 2 - в последний день месяца
    public void takeIntoAccountAdvancedSettingsWithChoicePaymentDate(String num){
        driver.findElement(By.xpath("//select[@id=\"loanParameters.paymentDateType\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanParameters.paymentDateType\"]/option[" + num + "]")).click();
    }
    //Учитываем расширенные настройки. Перенос даты, если дата платежа выпала на выходной: 1 - на след.раб.ден. 2 - на пред.раб.день
    public void takeIntoAccountAdvancedSettingsWithTransferPaymentDate(String num){
        driver.findElement(By.xpath("//label[@for=\"loanParameters.root_isWorkingDayTransferable\"]")).click();//кликаем галочку: "Переносить дату платежа.."
        driver.findElement(By.xpath("//select[@id=\"loanParameters.root_workingDayType\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanParameters.root_workingDayType\"]/option[" + num + "]")).click();
    }
    //Учитываем расширенные настройки. Отмечаем: последний платеж не может превышать аннуитент
    public void takeIntoAccountAdvancedSettingsWithLastPayment(){
        driver.findElement(By.xpath("//label[@for=\"loanParameters.tuneMonthAnnuity\"]")).click();
    }
    //Учитываем расширенные настройки. Платить сначала только проценты. time - срок платежей. num: 1 - кол-во месяцев. 2 - кол-во лет
    public void takeIntoAccountAdvancedSettingsPayOnlyPercent(String time, String num){
        driver.findElement(By.xpath("//label[@for=\"loanParameters.onlyPercentFirstly\"]")).click();//кликаем: Платить сначала только проценты
        driver.findElement(By.xpath("//input[@id=\"loanParameters.root_onlyPercentPeriodValue\"]")).clear();
        driver.findElement(By.xpath("//input[@id=\"loanParameters.root_onlyPercentPeriodValue\"]")).sendKeys(time);
        driver.findElement(By.xpath("//select[@id=\"loanParameters.root_onlyPercentPeriodType\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanParameters.root_onlyPercentPeriodType\"]/option[" + num + "]")).click();
    }
    //Выберите тип ставки: 2-изменяемая. count - кол-во изменений ставки. rate - массив ставок. num - массив как
    //будет изменяться ставка: 1 - каждый месяц. 2 -каждый год. 3 - с какого числа будет изменяться ставка
    public void chooseVariableRateTypeDropdownText(int count, String[] rate, String[] num, String[] date){
        driver.findElement(rateTypeDropdownText).click();
        driver.findElement(By.xpath("//select[@id=\"loanParameters.rateType\"]/option[2]")).click();
        driver.findElement(By.xpath("//input[@id=\"loanParameters.changedByDateRates0.rate\"]")).clear();
        driver.findElement(By.xpath("//input[@id=\"loanParameters.changedByDateRates0.rate\"]")).clear();
        driver.findElement(By.xpath("//input[@id=\"loanParameters.changedByDateRates0.rate\"]")).sendKeys(rate[0]);
        driver.findElement(By.xpath("//select[@id=\"loanParameters.changedByDateRates0.periodType\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanParameters.changedByDateRates0.periodType\"]/option[" + num[0] + "]")).click();

        if (count > 1){
            for (int i = 1; i < count; i++) {
                driver.findElement(By.xpath("//button[@data-bind=\"click : function(){changedByDateRates.push($root.newChangedByDateRate($data));},clickBuble : false\"]")).click();
                driver.findElement(By.xpath("//input[@id=\"loanParameters.changedByDateRates" + i + ".rate\"]")).clear();
                driver.findElement(By.xpath("//input[@id=\"loanParameters.changedByDateRates" + i + ".rate\"]")).clear();
                driver.findElement(By.xpath("//input[@id=\"loanParameters.changedByDateRates" + i + ".rate\"]")).sendKeys(rate[i]);
                driver.findElement(By.xpath("//select[@id=\"loanParameters.changedByDateRates" + i + ".periodType\"]")).click();
                driver.findElement(By.xpath("//select[@id=\"loanParameters.changedByDateRates" + i + ".periodType\"]/option[" + num[i] + "]")).click();
                driver.findElement(By.xpath("//input[@id=\"loanParameters.changedByDateRates" + i + ".startDate\"]")).clear();
                driver.findElement(By.xpath("//input[@id=\"loanParameters.changedByDateRates" + i + ".startDate\"]")).sendKeys(date[i]);
            }
        }
    }
    //Учесть комиссию. name - название комиссии. num1 - как часто начислять комиссию:
    // 1-единовременно.2-ежемесячно. 3-ежегодно. 4-при каждом платеже. 5-при досрочном платеже. amount - сумма комиссии
    //num2 - размер комиссии. 1-в рублях. 2-% от суммы кредита. 3-%от суммы задолжности. 4-% от суммы кредита увелич на.. 5-%ост от задолж увелич на..
    //percent - применяется, если Num2 == 3 или 4. percent показывает на какой процент будет увеличена коммиссия
    public void takeCommission(String name, String num1, String amount, String num2, String percent){
        driver.findElement(By.xpath("//button[@data-bind=\"click : function(){commissionParameters.push(newCommissionParameter());},clickBuble : false\"]")).click();
        driver.findElement(By.xpath("//input[@id=\"commissionParameters0.name\"]")).sendKeys(name);
        driver.findElement(By.xpath("//select[@id=\"commissionParameters0.periodicityType\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"commissionParameters0.periodicityType\"]/option[" + num1 + "]")).click();
        driver.findElement(By.xpath("//select[@id=\"commissionParameters0.relativeAmountType\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"commissionParameters0.relativeAmountType\"]/option[" + num2 + "]")).click();
        if (Integer.parseInt(num1) == 1 || Integer.parseInt(num1) == 4 || Integer.parseInt(num1) == 5) {
            if (Integer.parseInt(num2) == 2){
                driver.findElement(By.xpath("//input[@id=\"commissionParameters0.relativeRate\"]")).clear();
                driver.findElement(By.xpath("//input[@id=\"commissionParameters0.relativeRate\"]")).sendKeys(amount);
            }else {
                driver.findElement(By.xpath("//input[@id=\"commissionParameters0.fixedAmount\"]")).clear();
                driver.findElement(By.xpath("//input[@id=\"commissionParameters0.fixedAmount\"]")).sendKeys(amount);
            }
        }else{
            driver.findElement(By.xpath("//input[@id=\"commissionParameters0.relativeRate\"]")).clear();
            driver.findElement(By.xpath("//input[@id=\"commissionParameters0.relativeRate\"]")).sendKeys(amount);
        }
        if (Integer.parseInt(num2) > 3){
            driver.findElement(By.xpath("//input[@id=\"commissionParameters0.relativeAdditionPercent\"]")).clear();
            driver.findElement(By.xpath("//input[@id=\"commissionParameters0.relativeAdditionPercent\"]")).sendKeys(percent);
        }
    }
    //Учесть досрочное погашение. num1-периодичность. amount-сумма. date-дата. num2-пересчет графика платежей
    public void takeEarlyRepayment(String num1, String amount, String date, String num2){
        driver.findElement(By.xpath("//button[@data-bind=\"click : function(){loanAdvancedRepayments.push(newAdvancedRepayment());},clickBuble : false\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanAdvancedRepayments0.periodicityType\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanAdvancedRepayments0.periodicityType\"]/option[" + num1 + "]")).click();
        driver.findElement(By.xpath("//input[@id=\"loanAdvancedRepayments0.amount\"]")).clear();
        driver.findElement(By.xpath("//input[@id=\"loanAdvancedRepayments0.amount\"]")).sendKeys(amount);
        driver.findElement(By.xpath("//input[@id=\"loanAdvancedRepayments0.startDate\"]")).clear();
        driver.findElement(By.xpath("//input[@id=\"loanAdvancedRepayments0.startDate\"]")).sendKeys(date);
        driver.findElement(By.xpath("//input[@id=\"loanAdvancedRepayments0.startDate\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanAdvancedRepayments0.recalculationType\"]")).click();
        driver.findElement(By.xpath("//select[@id=\"loanAdvancedRepayments0.recalculationType\"]/option[" + num2 + "]")).click();
    }

}
