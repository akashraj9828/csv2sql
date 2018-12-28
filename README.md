# CSV2SQL
##### Clean java program to convert CSV to SQL(insert)

## Requirement:
- ####  Java

## Usage:
- Place the CSV file in the same directory as the java file.

- #### Windows user:
   -    ```sh
        $ run inputfile.csv outputfile.sql 
         ```
         Example.:
        ``` sh
        $ run calendar.csv calendar.sql holidays
        ```
- #### Other user:
    -   ```sh
        $ del CSV2SQL.class
        $ javac CSV2SQL.java
        $ java CSV2SQL inputfile.csv outputfile.sql tablename
        ```
         Example.:
        ``` sh
        $ del CSV2SQL.class
        $ javac CSV2SQL.java
        $ java CSV2SQL calendar.csv calendar.sql holidays
        ```
        
## Extras:
> Few options can be changed in code like.
> Boolean print_csv = false;  //show sql in console
> Boolean print_header = true;    //shows header in console
> Boolean verbose = true;
> Boolean ignoreError = false; //creates sql for faulty lines too
