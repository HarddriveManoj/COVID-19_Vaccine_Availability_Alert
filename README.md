# COVID-19_Vaccine_Availability_Alert
Auto Alert for vaccine availibility from COWIN Public APIs

**Step 1:Update application.properties**


spring.mail.default-encoding=UTF-8 
spring.mail.host=smtp.gmail.com # -> Put your SMTP Host name 
spring.mail.username=abc@gmail.com # -> If using gmail , please go to google allow settings and allow less security
spring.mail.password=password # -> Password
spring.mail.port=587


spring.mail.protocol=smtp
spring.mail.test-connection=false
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

email.from=abc@gmail.com
email.to=abc@gmail.com
email.cc=abc@gmail.com,abc@gmail.com
email.bcc=abc@gmail.com,abc@gmail.com

spring.cowin.districtidarray= 363
#choose your respective districts from districtmaster.json file
# PUNE DISTRICT IS 363

execution.interval=5
# Put this value in minutes, I have disabled this 

**Step 2: mvn clean install**

**Step 3:  open cmd and run -> java -jar target/GetVaccineAvailibilityV1-0.0.1-SNAPSHOT.jar**


**Output Email:**
As soon as vaccine appointment is uploaded, Email will content as below --> 


Center Name is::New Bhosari (18-44) :District::Pune-	::Available capacity is ::76	::Session date is::03-05-2021	::Minimum age limit is ::18	::Pincode is ::411026

