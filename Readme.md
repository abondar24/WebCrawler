# WebCrawler

A small utility which takes a search term as input, downloads results from google,parses links 
and prints top 5 used js libs amount the links.


# Assumptions

1. For performance reason only first page of google search is fetched
2. Utility takes input from console no parameters needed

# Build and run

```yaml
./gradlew clean build

java -jar build/libs/WebCrawler-0.1-SNAPSHOT.jar
```
