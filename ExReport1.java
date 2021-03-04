package org.seleniumhq.selenium.ApiTestProject;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static org.junit.Assert.assertEquals;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class ExReport1 {

	static ExtentTest test;
	static ExtentReports report;

	@BeforeClass
	public static void classSetup() {
		try {
			RestAssured.baseURI = "https://reqres.in";
			RestAssured.basePath = "/api";

			report = new ExtentReports(System.getProperty("user.dir") + "\\ExtentReportResults.html");
			test = report.startTest("ExtentDemo");

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	@Test
	//Get all users on the page 2
	public void getUsers() {
		//String stepName = "Show all users";
		Response resp = RestAssured.given()
				.header("content-type", "application/json")
				.when()
				.param("page", 2)
				.get("/users");
		resp.body().prettyPrint();

		try {
			assertEquals(resp.getBody().jsonPath().get("page").toString(), "2");
			test.log(LogStatus.PASS, "Sucessfully Get Users");			
		} catch (Exception ex) {
			test.log(LogStatus.FAIL, "Test Failed");
			//test.log(LogStatus.ERROR, stepName);
		}
	}

	@Test
	// Add a new user
	public void postTest() {
		Response resp = RestAssured.given()
				.accept("content-type")
				.body("{\"name\":\"ivonne\", \"job\":\"sdet\"}")
				.post("/users")
				.then()
				.extract()
				.response();
		resp.body().prettyPrint();

		try {
			assertEquals(201, resp.statusCode());
			test.log(LogStatus.PASS, "Sucessfully Post to add new user");
		} catch (Exception ex) {
			test.log(LogStatus.FAIL, "Test Failed");
		}
	}

	@Test
	// Update an user
	public void putTest() {

		JsonPath jsonPath = RestAssured.given()
				.accept(ContentType.JSON)
				.body("{\"name\":\"flor\", \"job\":\"tester\"}")
				.post("/users")
				.then()
				.extract()
				.jsonPath();
		;


		String idCreated = jsonPath.get("id").toString();

		Response resp = RestAssured.given()
				.log()
				.all()
				.accept(ContentType.JSON)
				.contentType(ContentType.JSON)
				.body("{ \"job\" : \"leader\" }")
				.put("/users/" + idCreated) // se utiliza el dato creado en el POST
				.then()
				.extract()
				.response();
		resp.body().prettyPrint();
		try {
			assertEquals(200, resp.statusCode());
			test.log(LogStatus.PASS, "Sucessfully Put to update an user");
		} catch (Exception ex) {
			test.log(LogStatus.FAIL, "Test Failed");
		}
	}

	@Test
	// Delete an user
	public void delete() {
		JsonPath jsonPath = RestAssured
				.given()
				.log()
				.all()
				.accept(ContentType.JSON)
				.body("{\"name\":\"flor\", \"job\":\"tester\"}")
				.post("/users")
				.then()
				.extract()
				.jsonPath();
		;

		String idCreated = (jsonPath.get("id").toString());
		Response resp = RestAssured.given()
				.header("Content-type", "application/json")
				.param("id", idCreated)
				.delete("/users/")
				.then().
				extract().
				response();
		resp.body().prettyPrint();

		try {
			assertEquals(204, resp.statusCode());
			test.log(LogStatus.PASS, "Sucessfully Delete User");		
		} catch (Exception ex) {
			test.log(LogStatus.FAIL, "Test Failed");
		}
	}

	
	@AfterClass
	public static void endTest() {
		report.endTest(test);
		report.flush();
	}
}