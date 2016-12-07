This RESTful service exposes an endpoint to upload an arbitrary text file.
The service parses the file (any uploaded file is assumed to be well formatted and contain only ascii characters) for the following information:
The total word count
The counts of each occurrence of a word
It returns the parsed information in the HTTP response body.

## Starting the application

Requires SBT 0.13.11 installed on your system.
Change directory to the main app folder in the terminal window. Then execute

```
> sbt run
```


## REST endpoints

Please use the Chrome browser plugin Postman, Paw (Mac OS X) or `curl`
to call the REST endpoints. Accepted content types are `text/plain` and `application/octet-stream`.

```
GET     /

# Word count endpoints
# The response content type is text/json

POST    /wordcount              # Request body: the text with the words to count
GET     /wordcount              # Responds with all previous word counts (header only)
GET     /wordcount/:id          # Responds with a full word count JSON object
```


## Remarks

The word counting algorithm uses the space char as a delimiter to process the byte stream in
frames. It expects every 1000 bytes (configurable) a space or fails
with a `BAD REQUEST` response. I've found a bug in the Akka-Streams framing
algorithm and reported it to the Akka dev team:
https://github.com/akka/akka/issues/20479
