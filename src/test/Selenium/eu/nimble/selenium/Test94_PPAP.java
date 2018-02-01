package eu.nimble.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class Test94_PPAP implements SeleniumInterface{
    @Override
    public void execute() {
        //Launch the website
        driver.get("http://localhost:9092/#/user-mgmt/login");

        // Get email and password input elements
        WebElement email = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"email\"]")));
        WebElement password = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"password\"]")));

        email.clear();
        email.sendKeys(emailAddressBuyer);

        password.clear();
        password.sendKeys(userPasswordBuyer);

        // Submit
        driver.findElement(By.xpath("/html/body/div[1]/nimble-app/nimble-login/credentials-form/form/button[1]")).click();

        // Wait until submit button is gone.In other words,wait until dashboard page is available.
        wait.until(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("/html/body/div/nimble-app/nimble-login/credentials-form/form/button[1]"))));

        // Go to search page
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/nimble-app/nav/button"))).click();
        driver.findElement(By.xpath("//*[@id=\"navbarNavAltMarkup\"]/ul[1]/li[3]/a")).click();

        // Search for QuantumExampleProduct
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"searchTerm\"]"))).sendKeys("QuantumExampleProduct");
        driver.findElement(By.xpath("/html/body/div/nimble-app/simple-search/simple-search-form/form/div[1]/span/button")).click();

        // Click (First one)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/nimble-app/simple-search/simple-search-form/form/div[3]/div[1]/div[2]/div[2]/div[1]/div/div"))).click();

        // PPAP
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/nimble-app/simple-search-details/div/h3/button"))).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/nimble-app/simple-search-details/div/product-bp-options/ul/li[3]/a"))).click();

        // Select a PPAP level
        Select level = new Select(wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/nimble-app/simple-search-details/div/product-bp-options/ppap/ppap-document-select/select"))));
        level.selectByVisibleText("Level 2");

        // Send
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/nimble-app/simple-search-details/div/product-bp-options/ppap/ppap-document-select/div[2]/button"))).click();

        // Check whether it is sent
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"bpOptionsMenuBuyer\"]")));

        // Logout
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"dropdownMenuUser\"]"))).click();
        driver.findElement(By.xpath("/html/body/div/nimble-app/nav/div/ul[2]/li/div/a[3]")).click();

    }
}
