# Github repository getter

Github repository getter can fetch repositories for selected user.

## Usage

To view the repositories, run the application and then go to http://localhost:8080/{github username}

If the user does not exist, a 404 error will be returned.

If we specify "Accept: application/xml" in the header, we will get a 406 error.