# proxim-android

Proxim helps to fight against COVID-19.

Proxim uses Bluetooth to scan the area around the device for other Proxim users and saves the data of these encounters.

When an Proxim user tests positive for COVID-19, the user is contacted by a healthcare authority and is asked to upload the data to create a map of potential secondary infections.

The healthcare authorities then analyse the data and contact the possibly newly infected for further measures (quarantine, testing).

The App is registered with a phone number. The phone numbers are available only for the healthcare authorities.


## How do we protect the user's privacy?

- User can remove all collected data, including the phone number.
- All data are saved locally on the user's device. Data are uploaded only with the user's consent after a healthcare authority's request.
- The scanning can be turned off manually at any time.
- The broadcasted _Device ID_ is changed every hour, so a user cannot be tracked with it. (Only our backend has a knowledge of which _Device ID_'s correspond to which phone number.)
- The data are kept on backend for 6 hours, then deleted.
- Proxim is developed open-source from day one.

