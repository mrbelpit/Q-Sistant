# Q-Sistant

### For the project to run, use these environmental variables:


### For the project to compile you need to assign values to these enviroment variables:

#### For the SecurityConstatns class:

- JWT_SECRET                                `(needs 32bit value like)`
- TOKEN_PREFIX                              `Bearer`
- TOKEN_HEADER_STRING                       `Authorization`
- JWT_TOKEN_EXPIRATION_TIME                 `3600000`
- KAFKA_SERVER_URI                          `127.0.0.1:9092 (default kafka address with default port)`
- FIRST_ADMIN_EMAIL                         `(any e-mail)`
- FIRST_ADMIN_PASSWORD                      `(any password)`

#### Calibration endpoints:

##### Setting workspace capacity (`/admin/calibrate/headcount` endpoint) can be done in two ways:
```xml
{
  "modifier" = ,
  "value" = 
}
```
- The modifier can be either `WORKPLACE_SPACE` or `WORKPLACE_CAPACITY`
	- For `WORKPLACE_SPACE` values are the number of people, who can be present, by default it is `250`, changing will set the the number for the `next day`.
	- For `WORKPLACE_CAPACITY` values can be between `0` and `100`, it is a percentage of the `250` person max, changing it this way will result in an `immediate change` in the number of people allowed in the office, if the value is higher then the current value. Otherwise, changing it will set the the number for the `next day`.

##### Setting distance between working stations (`/admin/calibrate/distance` endpoint):
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
  
##### Setting position of notification in the queue (`/admin/calibrate/notification` endpoint):
```xml
{
    "queueSetupNotificationNumber" : 2  
}
```
- The default value is `3`.
- The value must be higher then `0`.