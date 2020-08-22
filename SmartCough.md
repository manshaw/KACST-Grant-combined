# Summary
App can detect weather the user is covid patient or not based on his cough recording. For now we have launched the cough recording app to collect Cough Recording of its user. User maybe a covid-19 patient or not. User records his cough using his phone's microphone and then he answers some basic questions about himself before submtting the recording. The recording is sent to firebase storage and survey answers are stored in firebase database with timestamp. Data is collected anonmously.
Main Website for Smart Cough Android App is [Smart Cough Android](https://ncra.org.pk/SmartCough/)
and its Web App Version can be found here [Smart Cough Web](https://ciada.uqu.edu.sa/coughcovid/)

## Userflow
Userflow is simple and can be divided into following four steps, 
1. First time user is welcomed with some step-in instructions about how to cough safely.
1. Main app has single page design in which user can read some usage instructions and a push-and-hold button to record his cough.
1. After recording successfully, survey is opened in a pop-up window with some basic questions after which audio is finally submitted.
1. Submitted audio is then processed online and result is showed in a pop-up window.

## Firebase
We are using following firebase features,
- Storage
- Database

### Firebase Storage
Storage conntains a folder named 'recordings' in which recordings are saved with the name as timestamp and format is **3gp.**
Minimum recording time is set to **800 miliseconds.** Any recording less than 800 milisecond is considered as a false recording and user is asked to re-record the audio.

### Firebase Database
Database contains survey answers from each user. Root node is timestamp and it has **9 child nodes** amoung which **Disease** and **Symptoms** have further child nodes.

TimeStamp format is **[Day Month Date Hour:Minute:Second GMT+-Hour:Minute Year]**<br/>
For example **Fri May 08 03:18:27 GMT+05:00 2020**

It has following parameters, 

Parameter | Value
------------ | -------------
Age | Integer
Gender | Male _ Female
Smoke | True _ False
Breath | True _ False
Dry Cough | True _ False
Close Contact | True _ False
Disease | Chronic Lung Cancer _ Diabetes _ Heart Disease _ Obesity/Adipositas
Medical Condition | Healthy _ Might Have COVID _ COVID Patient _ COVID Survivour
Symtoms | Fever _ Feeling Tired or Week _ Diarreah _ Headache _ Lost of Taste or Smell

## Permissions
Only 2 permissions are required which are pretty obvious;
* Record Audio
* Internet

## Data Privacy
Data is collected anonmously as we don't ask users name, address, phone number or any kind of contact details. We only ask age and gender and there is no GPS based location or any location tracking in our app.
Further priacy policy can be read from here,
[Privacy Policy](https://ncra.org.pk/SmartCough/privacy-policy.html)

## Language Support
The app is currently available in **English** and **Arabic**. We are working continously to add more languages.

## Libraries Used
Following libraries are used in this app to make it more appealing,
* [Better Spinner](https://github.com/Lesilva/BetterSpinner)
* [Gif Drawable](https://github.com/koral--/android-gif-drawable)

