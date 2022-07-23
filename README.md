# Twitter API Code Challenge Notes

[//]: # ( date: 07/23/22 )

## 1. Overview

- This document contains documentation and links for setting up, running, and maintaining:
    1. A Java service to access a Twitter stream API, compute various statistics for the random tweets that it provides, and provide access to the statistics via an HTTPS API call.
    2. A Svelte Web application to display those results on demand by calling the aforementioned API.

### 1.1. Table of Contents

- [1. Overview](#1-overview)
  - [1.1. Table of Contents](#11-table-of-contents)
- [2. Specification](#2-specification)
- [3. Folders and Files](#3-folders-and-files)
- [4. Usage and Testing](#4-usage-and-testing)
  - [4.1. Setup](#41-setup)
  - [4.2. Manual Testing](#42-manual-testing)
  - [4.3. Automated Unit Tests](#43-automated-unit-tests)
- [5. Next Steps / To Do List](#5-next-steps--to-do-list)
  - [5.1. To Do before submission](#51-to-do-before-submission)
  - [5.2. Future features and steps for consideration](#52-future-features-and-steps-for-consideration)
- [6. Design diagrams](#6-design-diagrams)
  - [6.1. twitter-api Web API](#61-twitter-api-web-api)
  - [6.2. twitter-ui Web Application](#62-twitter-ui-web-application)
- [7. Twitter APIs](#7-twitter-apis)
  - [7.1. Using Postman to access Twitter APIs](#71-using-postman-to-access-twitter-apis)
  - [7.2. Run the solution projects under Docker](#72-run-the-solution-projects-under-docker)
- [8. References](#8-references)

[//]: # ( spell-checker: ignore blazor choco dockerignore plantuml )

## 2. Specification

- Your app should consume this sample stream and keep track of the following:
  - Total number of tweets received
  - Top 10 Hashtags
- Your app should also provide some way to report these values to a user (periodically log to terminal, return from RESTful web service, etc).
- If there are other interesting statistics you’d like to collect, that would be great. There is no need to store this data in a database; keeping everything in-memory is fine. That said, you should think about how you would persist data if that was a requirement.
- It’s very important that when the application receives a tweet it does not block statistics reporting while performing tweet processing. Twitter regularly sees 5700 tweets/second, so your app may likely receive 57 tweets/second, with higher burst rates. The app should process tweets as concurrently as possible to take advantage of available computing resources.

## 3. Folders and Files

| **Folder or File** | **Description**                                                                             |
| ------------------ | ------------------------------------------------------------------------------------------- |
| twitter-ui         | Folder containing Web app to display Twitter data accumulated provided by our API           |
| twitter-api        | Folder containing Web service to collect and process tweets from the Twitter sampled stream |
| `.dockerignore`    | List of files that Docker should ignore and not package                                     |
| `.gitignore`       | List of files that Git should ignore and not track                                          |
| `README.md`        | This documentation file                                                                     |

## 4. Usage and Testing

### 4.1. Setup

- Create an environment variable `STREAM_BEARER_TOKEN` with your Twitter API bearer token
- Load the source folder(s) into your favorite IDE

### 4.2. Manual Testing

- Select project `twitter-api` for the Web API service to collect tweet data and provide it in an API
  - Select `IIS Express` as Build Target on drop down (could also select `Docker` if you have Docker Desktop and WSL installed)
  - Select preferred Web Browser using Build Target drop down if required
  - Run (`Ctrl+F5`) or Debug (`F5`) the project, which should open a browser with the Swagger API
    - Refer to the Output window to see log messages such as for the Tweet processing
    - Use Swagger in the browser to execute the GET call to the API endpoint
      - Inspect the response body
      - Execute the API call repeatedly to see increasing numbers in stats
    - Test the API from the command line `curl -X 'GET' 'https://localhost:44355/api/GetSampledStream' -H 'accept: text/plain'`
- Select project `twitter-ui` for the Web App User Interface that calls the Web API (which must still be running)
  - Select `IIS Express` as Build Target (could also select `Docker` if you have Docker Desktop and WSL installed)
  - Select preferred Web Browser using Build Target drop down if required
  - Run (`Ctrl+F5`) or Debug (`F5`) the project, which should open a browser with the Swagger API
    - View the statistics
- Stop debugging (`Shift` `F5`)

### 4.3. Automated Unit Tests

- Select project `twitter-ui`
  - Note that all browser Unit Tests are currently hard coded to use Chrome (see To Do List)
  - Select `IIS Express` as Build Target on drop down (could also select `Docker` if you have Docker Desktop and WSL installed)
  - Start project without Debugging (`Ctrl+F5`) to show default Web page on localhost
    - If this is not run then the Selenium tests of the results web page will be skipped
      - Chrome will still load and exit a couple of times, but tests will still succeed
    - If this is run then the Selenium based tests of the results web page will be performed
  - Run all automated tests (`Ctrl+R, A`) or open the tests from Test Explorer (`Ctrl+E, T`) and run selected tests

## 5. Next Steps / To Do List

### 5.1. To Do before submission

- [ ] Create twitter-api project to process calls to Twitter API
  - [ ] Replace controller with dummy stream stats controller
  - [ ] Add Test suite project and basic tests
  - [ ] Add strategic exception handling
  - [ ] Stats for API result to use shared class
    - [ ] Return extra stats
  - [ ] Create background task to attach to Twitter stream
    - [ ] Add dummy tweets to concurrent queue
    - [ ] Add incoming tweets to concurrent queue
  - [ ] Create background service to pull incoming tweets from concurrent queue
    - [ ] Loop through tweets extracting hashtags
    - [ ] Add / update totals using a dictionary
      - [ ] Add / update top 10 tag list
- [ ] Create twitter-ui project to display twitter hashtag statistics
  - [ ] Replace home page with dummy stream stats
  - [ ] Add Test suite project and basic tests
  - [ ] Add Index Page Object to simplify testing
  - [ ] Add strategic exception handling and tests
  - [ ] Stats for API result to use shared class
  - [ ] Call collector API to retrieve latest stats
- [ ] Documentation
  - [ ] Usage and testing section 4
  - [ ] Update diagrams with the latest design
- [ ] Submission email
  - [ ] Set Twitter API bearer token in `STREAM_BEARER_TOKEN` environment variable
  - [x] GitHub URL and authentication (make repo public)

### 5.2. Future features and steps for consideration

- Use feature branches, merges, and pull requests for multi-user development, rather than just working on `main` branch!

| Priority | Category     | Effort | Description                                                                                                           |
| :------: | ------------ | :----: | --------------------------------------------------------------------------------------------------------------------- |
|    H     | Security     |   M    | Add OAUTH to secure the main API call                                                                                 |
|    H     | Tests        |   M    | Test and document running of tests using command line for CI/CD automation                                            |
|    H     | Architecture |   M    | Improve exception handling for edge cases, network errors etc.                                                        |
|    M     | Security     |   M    | Add OAUTH to secure Swagger UI                                                                                        |
|    M     | Tooling      |   M    | Use gRPC for improving service performance and efficiency                                                             |
|    M     | Tooling      |   M    | Use GraphQL for more advanced APIs                                                                                    |
|    M     | Tooling      |   S    | Create deployment automation script / Docker compose file for Docker containers                                       |
|    M     | Architecture |   M    | Add ability to restart background services and tasks after a serious error                                            |
|    M     | Performance  |   S    | SampledStreamStats.UpdateTopHashtags method to add to the top 10 table could:                                         |
|          |              |        | a) Check that the count is greater than the count of the lowest entry before doing any other checks                   |
|          |              |        | b) work upwards from the lowest count rather than downwards from the highest count                                    |
|    M     | Performance  |   M    | Create multiple TweetBlock processors that can each run concurrently                                                  |
|    M     | Performance  |   S    | Batch up (concatenate) incoming tweets before adding them as a block to the queue and then deserialize them as a list |
|    M     | Architecture |   M    | Move some of the hard coded values and URLs to configuration files                                                    |
|    M     | Tests        |   S    | Add testing of Svelte rendered pages                                                                                  |
|    M     | Tests        |   M    | Add more unit tests (always)                                                                                          |
|    M     | Tests        |   M    | Enhance Web UI tests to also run in FireFox and Edge                                                                  |
|    M     | Tests        |   M    | Finish setting up unit tests to run in parallel                                                                       |
|    M     | UI           |   S    | Add auto refresh of the Web app page and a combo box to control the frequency                                         |
|    L     | UI           |   S    | Handle any multi-cultural and multi-lingual requirements such as date and number formatting                           |
|    L     | Tests        |   S    | Handle any remaining edge cases of running the date and time tests at exactly midnight                                |

## 6. Design diagrams

### 6.1. twitter-api Web API

```mermaid
flowchart LR
  A("fa:fa-chrome External Application\ncalling API") <-->|Stats| PA
  B("fa:fa-twitter \nTwitter\nStream API") -->|Stream| CA
  subgraph twitter-api [.]
    subgraph Shared Data
      SA[Tweet queue]
      SB[Top 10 Hashtags]
      SC[Other Stats]
    end
    subgraph Collector
      CA(Tweet\nCollector) -->|Tweet data| SA
    end
    subgraph Updater
      SA -->|Tweet data| UA[[Parse out tweets]]
      UA --> UB[[Increment\nHashtag\nCounts]]
      UB <-->|AddOrUpdate| BA["All Hashtag Counts\n(ConcurrentDictionary)"]
      UB -->|Hashtag\n and Count| UC{Hashtag\nIn\nTop 10}
      UB --> SC
      UC -->|Yes| UD[[Replace\nin Top 10]]
      UD --> SB
      SB -.->|read| UC
    end
    subgraph Provider
      SB -.->|read| PA[GetStreamStats\nAPI call]
      SC -.->|read| PA
    end
  end
```

### 6.2. twitter-ui Web Application

```mermaid
flowchart LR
  A("fa:fa-firefox Web Browser") <--> AA
  subgraph twitter-ui
    AA(Main)
  end
  AA[[Build Web Page]] <-->|Stats| PA
  subgraph twitter-api
    PA[GetStreamStats]
  end
```

## 7. Twitter APIs

- [Apply for API access](https://developer.twitter.com/en/apply-for-access)

  ```sh
  # Sample results with example values from Twitter API documentation
  API Key: QAktM6W6DF6F7XXXXXX
  API Key Secret: AJX560A2Omgwyjr6Mml2esedujnZLHXXXXXX
  Bearer Token: AAAAAAAAAAAAAAAAAAAAAL9v6AAAAAAA99t03huuqRYg0mpYAAFRbPR3XXXXXXX
  ```

- Test from command prompt

  ```powershell
  # Set bearer token for testing access (example value from Twitter API documentation)
  $env:BEARER_TOKEN = 'AAAAAAAAAAAAAAAAAAAAAL9v6AAAAAAA99t03huuqRYg0mpYAAFRbPR3XXXXXXX'
  # Test API access
  curl -X GET "https://api.twitter.com/2/tweets/sample/stream" -H "Authorization: Bearer ${env:BEARER_TOKEN}"
  ```

### 7.1. Using Postman to access Twitter APIs

- Install Postman using Chocolatey
  - `choco install postman -y`
- Import collection from [Twitter API v2 collection](https://github.com/twitterdev/postman-twitter-api)
- Set up consumer keys and tokens in Environment

  ```sh
  # Example values from Twitter API documentation
  consumer_key: `QAktM6W6DF6F7XXXXXX`
  consumer_secret: `AJX560A2Omgwyjr6Mml2esedujnZLHXXXXXX`
  access_token: `1995XXXXX-0NGqVhk3s96IX6SgT3H2bbjOPjcyQXXXXXXX`
  token_secret: `rHVuh7dgDuJCOGeoe4tndtjKwWiDjBZHLaZXXXXXX`
  bearer_token: `AAAAAAAAAAAAAAAAAAAAAL9v6AAAAAAA99t03huuqRYg0mpYAAFRbPR3XXXXXXX`
  ```

- Click `Send` and check for 200 OK response and then `Cancel`
- Response body will be empty because Twitter does not fill streams  for Postman
- Use Code | cURL to view the command, then copy and run it

### 7.2. Run the solution projects under Docker

```sh
# Set up the Twitter API token
export STREAM_BEARER_TOKEN=<token>
# Run the collector container image using the token
docker run -e "STREAM_BEARER_TOKEN" -d <collector image id>
# Show the latest log messages to monitor its progress
docker container logs <container id>
# Run a shell in the container for troubleshooting
docker container exec -i <container id> bash
```

## 8. References
