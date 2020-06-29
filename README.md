# Q-Sistant

## For the project to compile and run, use these environmental variables:

### For the SecurityConstants class:

- JWT_SECRET                                `(needs 32bit value like)`
- TOKEN_PREFIX                              `Bearer`
- TOKEN_HEADER_STRING                       `Authorization`
- JWT_TOKEN_EXPIRATION_TIME                 `3600000`
- KAFKA_SERVER_URI                          `127.0.0.1:9092 (default kafka address with default port)`
- FIRST_ADMIN_EMAIL                         `(any e-mail, hosted  version uses "bob@bob.com")`
- FIRST_ADMIN_PASSWORD                      `(any password, hosted  version uses "bob")`
- OFFICE_IMAGE_URL                          `https://github.com/mrbelpit/Q-Sistant/blob/master/layout/accenture_layout.jpg?raw=true`
- ACCESS_DENIED_PICTURE_FILEPATH                      `(any picture you like)`


## Image processing for workstation distribution:
### Input image:
The application uses the layout image of the office to populate a list of distributable workstations.  
**The image has some constraints:**  
Workstations need to be designated by **coherent blue shapes**. This blue colour is **ideally rgb(0, 0, 255)**, but there can be a **maximum of 5 point error in the intensity** of each three colours. This means that **a pixel with colour rgb(5, 5, 250) is also considered a workstation pixel**. Make sure that **only workstations have the exact colour blue** on the image. As long as shapes are coherent (you can make a path between any two pixels in them by only going through blue pixels) they are considered a single workstation.  
Here is a correct office layout:
![A correct office layout](https://github.com/mrbelpit/Q-Sistant/blob/master/layout/accenture_layout.jpg?raw=true)  
### Output images:  
#### HR deparment's complete office layout <a name="hrdepartment"></a>:  
The HR department (admins) has the ability to query the current status of the office by accessing the **/admin/layout** endpoint. This means that they can see occupied, reserved, free and unavailable workstations in the office.
- A workstation is:
  - **RESERVED (orange)** if it has been assigned to a user, but the user has not yet entered the office.
  - **OCCUPIED (red)** if it has been assigned to a user, and the user has already entered the office.
  - **UNAVAILABLE (pink)** if it is within the specified minimum range of a RESERVED or OCCUPIED workstation.
  - **FREE (green)** if it has not been RESERVED, OCCUPIED and is not UNAVAILABLE  
![Example generated office layout image](https://github.com/mrbelpit/Q-Sistant/blob/master/layout/adminlayout.jpg?raw=true)
#### Employee's assigned workstation displayed on the office layout:  
Users that have gained access to enter the office get an assigned workstation. They can view the position of their workstation by accessing the **/office/station** endpoint. The accessing user's workstation is designated by a **relatively big red rectangle**.
![A user's assigned workstation](https://github.com/mrbelpit/Q-Sistant/blob/master/layout/officestation.jpg?raw=true)
### The process of workstation distribution:
**Every day at midnight**, the application **processes the office layout image** specified by the OFFICE_IMAGE_URL environment variable. This means that it **generates a list of workstations** and for each individual station it **stores the stations that are closer than the specified minimum distance** between distributed workstations. After this, when a user reserves a workstation, other workstations that are too close become unavailable. At every distribution (either straight away or from a queue) the application selects a workstation from the list that **has the least free neighbors**. This means that **if workstation A is free and has one free and one unavailable neighbor** and **workstation B is also free but has more than one free neighbors**, **A will be distributed instead of B**. If there are no free workstations, the user will be placed in the queue.
## Admin Controller
- This endpoints available only users with role admin.

### Calibration endpoints:

#### Setting workspace capacity ( PUT `/admin/calibrate/headcount` endpoint) can be done in two ways:
```xml
{
  "modifier" = ,
  "value" = 
}
```
- The modifier can be either `WORKPLACE_SPACE` or `WORKPLACE_CAPACITY`
	- For `WORKPLACE_SPACE` values are the number of people, who can be present, by default it is `250`, changing will set the the number for the `next day`.
	- For `WORKPLACE_CAPACITY` values can be between `0` and `100`, it is a percentage of the `250` person max, changing it this way will result in an `immediate change` in the number of people allowed in the office, if the value is higher then the current value. Otherwise, changing it will set the the number for the `next day`.

#### Setting distance between working stations ( PUT `/admin/calibrate/distance` endpoint):
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
  
#### Setting position of notification in the queue ( PUT `/admin/calibrate/notification` endpoint):
```xml
{
    "queueSetupNotificationNumber" : 2  
}
```
- The default value is `3`.
- The value must be higher then `0`.

### User management endpoints:

#### Able to register user wit different roles ( POST `/admin/user/register` endpoint):
```xml
{
    "firstName": "Bela",
    "lastName": "Nagy",
    "password": "bn",
    "email": "bela.nagy@gmail.com",
    "cardId":72,
    "userRole": "VIP"
}
```
- At this endpoint you can register user, with different roles.
  - There are 3 options as `userRole` to register: `ADMIN`, `VIP` and `EMPLOYEE`
  
  
#### Able to register a list of users wit different roles ( POST `/admin/users/register` endpoint):
```xml
[
    {
        "firstName": "Bela",
        "lastName": "Nagy",
        "password": "bn",
        "email": "bela.nagy@gmail.com",
        "cardId":72,
        "userRole": "VIP"
    },
    {
        "firstName": "Virag",
        "lastName": "Kiss",
        "password": "vk",
        "email": "virag.kis@gmail.com",
        "cardId":73,
        "userRole": "ADMIN"
    }
]
```
- At this endpoint you can register a list of users, with different roles.
  - There are 3 options as `userRole` to register: `ADMIN`, `VIP` and `EMPLOYEE`
  
#### Able to delete an user with the provided userId ( DELETE `/admin/user/{userId}` endpoint):
- At this endpoint you can delete an user, with an existing user id.
  
#### Able to filter users wit different roles ( GET `/admin/users/{filter}` endpoint):
- The different filters list the `users` with different roles, and `all` list all the users.
  - There are 4 filter available: `employee`, `admin`, `vip` and `all`
  
### Information endpoints:
#### Provide general and specific information about the office ( GET `/admin/info` endpoint):
-  Information:
   - `maxWorkplaceSpace` the number of maximum work station
   - `workspaceCapacityPercentage` percentage of people(employee and admin), who can enter into the building
   - `maxWorkerAllowedToEnter` the number of people(employee and admin), who able to enter the building
   - `workersCurrentlyInOffice` the number of people(employee, admin, vip),who are in the building
   - `freeSpace` the number of available free space(for employee and admin) in the building
   - `employeesInTheBuilding` the list of `users` who are currently in the building
   
#### Provide picture with the office's workstations with different status ( GET `/admin/layout` endpoint):
- [See here](#hrdepartment)

## For all endpoints see [swagger](http://qsistant-env-1.eba-5xbc6q7e.eu-west-3.elasticbeanstalk.com/swagger-ui.html)
