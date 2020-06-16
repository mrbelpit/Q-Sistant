For the project to run, use these environmental variables:


For the project to compile you need to assign values to these enviroment variables:

For the SecurityConstatns class:
JWT_SECRET (needs 32bit value like)
TOKEN_PREFIX
TOKEN_HEADER_STRING
JWT_TOKEN_EXPIRATION_TIME

we use:
TOKEN_PREFIX="Bearer"
TOKEN_HEADER_STRING="Authorization"
JWT_TOKEN_EXPIRATION_TIME=3600000


Setting workspace capacity (/office/admin/calibrate endpoint) can be done in two ways:

{
  "modifier" = ,
  "value" = 
}

the modifier can be either "WORKPLACE_SPACE" or "WORKPLACE_CAPACITY"
for "WORKPLACE_SPACE" values are the number of person, who can be present, by default it is 250, changing will set the the number for the next day
for "WORKPLACE_CAPACITY" values can be between 0 and 100, it is a percentage of the 250 person max, changing it this way will result in an immediate chenge is numger of people allowed in the office.


