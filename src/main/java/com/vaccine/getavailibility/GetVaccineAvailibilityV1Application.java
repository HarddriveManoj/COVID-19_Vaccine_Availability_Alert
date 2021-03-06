package com.vaccine.getavailibility;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vaccine.getavailibility.configuration.PropertiesCache;
import com.vaccine.getavailibility.model.Center;
import com.vaccine.getavailibility.model.MailMessageRequest;
import com.vaccine.getavailibility.model.Response;
import com.vaccine.getavailibility.model.Root;
import com.vaccine.getavailibility.model.Session;
import com.vaccine.getavailibility.service.MailServiceImpl;

@SpringBootApplication
public class GetVaccineAvailibilityV1Application {





	public static void main(String[] args) throws ParseException, AddressException, MessagingException, IOException {

		GetVaccineAvailibilityV1Application getVaccineAvailibilityV1Application = new GetVaccineAvailibilityV1Application();

			while (true) {
				try {
					String formattedDate = getVaccineAvailibilityV1Application.changeDate(1);
					System.out.println("----------------------->  " + formattedDate);
					try {
						getVaccineAvailibilityV1Application.makeAvailibilityCall(formattedDate, PropertiesCache.getInstance().getProperty("spring.cowin.districtidarray").split(","));
					} catch (AddressException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				Thread.sleep(Integer.parseInt(PropertiesCache.getInstance().getProperty("execution.interval")) * 4 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}


	}

	private String changeDate(int i) {
		LocalDateTime ldt = LocalDateTime.now().plusDays(i);
		DateTimeFormatter formmat1 = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);

		String formattedDate = formmat1.format(ldt);
		System.out.println(formattedDate);
		return formattedDate;
	}

	void makeAvailibilityCall(String date, String[] districtId)
			throws AddressException, MessagingException, IOException {
		List<Response> responseList = new ArrayList();

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		httpHeaders.set("Accept-Language", "en_US");
		httpHeaders.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0");
		HttpEntity<String> request = new HttpEntity<String>(httpHeaders);

		boolean responseBool = false;
		for (String str : Arrays.asList(districtId)) {

			String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id="
					+ str + "&date=" + date;
			System.out.println("URL: " + url);
			System.out.println("--------------------");
			ResponseEntity<Root> result = restTemplate.exchange(url, HttpMethod.GET, request, Root.class);
			System.out.println("REsponse: " + result.toString());
			if(result.getBody().centers != null) {
				for (Center center : result.getBody().centers) {
					for (Session session : center.sessions) {
						if (session.min_age_limit < 45 && session.available_capacity > 0) {
							Response response = new Response();
							response.setMin_age_limit(session.min_age_limit);
							response.setName("Center Name is::" + center.name + " :District::" + center.district_name
									+ "-" + session.vaccine);
							response.setDate("::Session date is::" + session.date);
							response.setAvailable_capacity(session.available_capacity);
							response.setVaccine(session.vaccine);
							response.setPincode(center.pincode);
							responseBool = true;
							System.out.println("Center name is ::" + center.name + ":" + center.district_name
									+ "::Available capacity::" + session.available_capacity + "::: Date-->" + session.date
									+ "::" + "Minimum Age Limit::" + session.min_age_limit);
							responseList.add(response);

						}
					}

				}
			}
		}



		System.out.println("responseBool::" + responseBool + new Date().toGMTString() +"::Size::"+responseList.size());
		if (responseBool) {

			sendEmail(responseList);
		}
	}

	void sendEmail(List<Response> responseList) throws AddressException, MessagingException, IOException {
		MailServiceImpl mailServiceImpl = new MailServiceImpl();
		mailServiceImpl.sendmail(responseList);

	}

}
