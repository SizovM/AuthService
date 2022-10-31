package net.absoft;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import static org.testng.Assert.*;

public class AuthenticationServiceTest extends BaseTest {

private AuthenticationService authenticationService;
@BeforeClass(groups = "positive")
public void setup(){
  authenticationService=new AuthenticationService();

}

@Test(groups = "positive")
public void testSample() throws InterruptedException{
  Thread.sleep(2000);
  System.out.println("testSample: " + new Date());

}


  @Test(description= "Test Succesfull Authentication",
 groups = "positive")

  @Parameters({"email-address","password"})
  public void testSuccessfulAuthentication(@Optional("user1@test.com") String email,@Optional("password") String password) throws InterruptedException {
      Response response = new AuthenticationService().authenticate("user1@test.com", "password1");

    assertEquals(response.getCode(), 200, "Response code should be 200");
   assertTrue(validateToken(response.getMessage()),
    "Token should be the 32 digits string. Got: " + response.getMessage());

  }
@DataProvider(name="invalidLogins")
public Object[][] invalidLogins(){
return new Object[][]{
        new Object[] {"user1@test.com","wrong password",new Response(401,"Invalid email or password")},
        new Object[] {"","password",new Response(400,"Email shoud not be empty string")}

};

}

  @Test(
          groups = "negative",
          dataProvider = "invalidLogins"
  )

  public void testInvalidAuthentication(String email, String password, Response expectedResponse) {
    Response actualResponse =  authenticationService
            .authenticate("user1@test.com", "wrong_password1");
    SoftAssert sa = new SoftAssert();
    sa.assertEquals(actualResponse.getCode(), expectedResponse.getCode(), "Response code should be 401");
    sa.assertEquals(actualResponse.getMessage(), expectedResponse.getMessage(),
            "Response message should be \"Invalid email or password\"");
    sa.assertAll();
  }




  @Test(
 groups = "negative"
 )

  public void testAuthenticationWithWrongPassword() {
    Response response =  authenticationService
       .authenticate("user1@test.com", "wrong_password1");
   SoftAssert sa = new SoftAssert();
    sa.assertEquals(response.getCode(), 501, "Response code should be 401");
    sa.assertEquals(response.getMessage(), "Broken Invalid email or password",
        "Response message should be \"Invalid email or password\"");
    sa.assertAll();
  }

  @Test(priority=3,groups="negative"
          )


  public void testAuthenticationWithEmptyEmail() {
    Response response =  authenticationService.authenticate("", "password1");
    assertEquals(response.getCode(), 400, "Response code should be 400");
    assertEquals(response.getMessage(), "Email should not be empty string",
        "Response message should be \"Email should not be empty string\"");
  }

  @Test(groups="negative")
  public void testAuthenticationWithInvalidEmail() throws InterruptedException {
    Response response = authenticationService.authenticate("user1", "password1");
    assertEquals(response.getCode(), 400, "Response code should be 200");
    assertEquals(response.getMessage(), "Invalid email",
        "Response message should be \"Invalid email\"");

  }

  @Test(groups="negative",priority=2,
  dependsOnMethods = {"testAuthenticationWithInvalidEmail"})
  public void testAuthenticationWithEmptyPassword() {
    Response response =  authenticationService.authenticate("user1@test", "");
    assertEquals(response.getCode(), 400, "Response code should be 400");
    assertEquals(response.getMessage(), "Password should not be empty string",
        "Response message should be \"Password should not be empty string\"");
  }

  private boolean validateToken(String token) {
    final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(token);
    return matcher.matches();
  }
}
