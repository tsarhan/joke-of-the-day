# Joke of the day service
Web service for creating, retrieving, updating and deleting jokes. The app returns a random joke from the jokes that are in the database. A joke has the required fields joke & date and an optional description field. 

# Run Steps
1. Clone or download repo https://github.com/tsarhan/joke-of-the-day
2. In root directory of the project run the below command to start up the app
```
    mvn spring-boot:run
```
3. This service exposes REST endpoints for executing CRUD operations on jokes. 
  - The base url is /jokeoftheday
  - Supported Http methods:
    1. GET: use with base URL to get a random joke if any exist
    2. POST: use with base URL to create a joke with payload like below

    ```json
    {
     "joke": "Why did the Software Engineer Cross the road? To see if a car accident can be reproduced",
     "date": "2025-04-28",
     "description": "Cross the Road Joke"
    }
    ```
    3. DELETE: use with base URL above with id like so  /jokeoftheday/2bcb772c-64d3-433e-9c32-0229a36f78d8
    4. PUT: use with base URL above with id like so  /jokeoftheday/2bcb772c-64d3-433e-9c32-0229a36f78d8
    ```json
    {
      "id": "2bcb772c-64d3-433e-9c32-0229a36f78d8",
     "joke": "A SQL query goes into a bar, walks up to two tables and asks: Can I join you?",
     "date": "2025-04-28",
     "description": "Cross the Road Joke"
    }
    ```


     
