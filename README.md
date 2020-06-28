# Q-Sistant

## For the project to compile and run, use these environmental variables:

### For the SecurityConstants class:

- JWT_SECRET                                `(needs 32bit value like)`
- TOKEN_PREFIX                              `Bearer`
- TOKEN_HEADER_STRING                       `Authorization`
- JWT_TOKEN_EXPIRATION_TIME                 `3600000`
- KAFKA_SERVER_URI                          `127.0.0.1:9092 (default kafka address with default port)`
- FIRST_ADMIN_EMAIL                         `(any e-mail)`
- FIRST_ADMIN_PASSWORD                      `(any password)`

## Image processing for workstation distribution:
### Input image:
The application uses the layout image of the office to populate a list of distributable workstations.  
**The image has some constraints:**  
Workstations need to be designated by **coherent blue shapes**. This blue colour is **ideally rgb(0, 0, 255)**, but there can be a **maximum of 5 point error in the intensity** of each three colours. This means that **a pixel with colour rgb(5, 5, 250) is also considered a workstation pixel**. Make sure that **only workstations have the exact colour blue** on the image. As long as shapes are coherent (you can make a path between any two pixels in them by only going through blue pixels) they are considered a single workstation.  
Here is a correct office layout:
![A correct office layout](https://github.com/mrbelpit/Q-Sistant/blob/master/layout/accenture_layout.jpg?raw=true)  
### Output images:  
#### HR deparment's complete office layout:  
The HR department (admins) has the ability to query the current status of the office by accessing the **/admin/layout** endpoint. This means that they can see occupied, reserved, free and unavailable workstations in the office.
- A workstation is:
  - **RESERVED (orange)** if it has been assigned to a user, but the user has not yet entered the office.
  - **OCCUPIED (red)** if it has been assigned to a user, and the user has already entered the office.
  - **UNAVAILABLE (pink)** if it is within the specified minimum range of a RESERVED or OCCUPIED workstation.
  - **FREE (green)** if it has not been RESERVED, OCCUPIED and is not UNAVAILABLE  
![Example generated office layout image](https://github.com/mrbelpit/Q-Sistant/blob/master/layout/adminlayout.jpg?raw=true)
#### Employee's assigned workstation displayed on the office layout:  
Users that have gained access to enter the office get an assigned workstation. They can view the position of their workstation by accessing the **/office/station** endpoint. The accessing user's workstation is designated by a relatively big red rectangle.
![A user's assigned workstation](https://github.com/mrbelpit/Q-Sistant/blob/master/layout/officestation.jpg?raw=true)

## Calibration endpoints:

### Setting workspace capacity (`/admin/calibrate/headcount` endpoint) can be done in two ways:
```xml
{
  "modifier" = ,
  "value" = 
}
```
- The modifier can be either `WORKPLACE_SPACE` or `WORKPLACE_CAPACITY`
	- For `WORKPLACE_SPACE` values are the number of people, who can be present, by default it is `250`, changing will set the the number for the `next day`.
	- For `WORKPLACE_CAPACITY` values can be between `0` and `100`, it is a percentage of the `250` person max, changing it this way will result in an `immediate change` in the number of people allowed in the office, if the value is higher then the current value. Otherwise, changing it will set the the number for the `next day`.

### Setting distance between working stations (`/admin/calibrate/distance` endpoint):
```xml
{
    "unit" : "METER",
    "value" : 3
}
```
- The modifier can be either `METER` or `METRE`
  - We support only meter as unit but the program is easily extendable with other input units.
  - The value can be between `0` and `10`.
  - The default value is `5`. After a successful setup, it will set the the distance for the `next day`.
  
### Setting position of notification in the queue (`/admin/calibrate/notification` endpoint):
```xml
{
    "queueSetupNotificationNumber" : 2  
}
```
- The default value is `3`.
- The value must be higher then `0`.
