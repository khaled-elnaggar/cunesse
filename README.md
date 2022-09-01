# CuNesse

Welcome to CuNesse: [Cucumber] and [FitNesse] integration.

CuNesse is simply a "Test System plugin" for [FitNesse] to enable [cucumber-jvm](https://cucumber.io/docs/installation/java/) features in writing and executing Gherkin scripts, all the while leveraging [FitNesse]'s user-friendly wiki web server, versioning and reporting capabilities.


## Demo
First off, you have to clone this repo and navigate inside of it
```sh
git clone https://github.com/khaled-elnaggar/cunesse.git
cd cunesse
```
This repo contains a toy demo, to run FitNesse server locally, simply run
```sh
mvn test -Pfitnesse-server
```
Maven wrapper is also included in case you do not have maven installed, simply run

```sh
.\mvnw test -Pfitnesse-server
```

Now go to `http://localhost:8003/` on your web browser to find the familiar FitNesse landing page.

Go specifically to `http://localhost:8003/FrontPage.CucumberTestSystem`, the TestSuite we prepared as a demo.

Now try playing around with the TestPages, add new scenarios and modify the step definitions found in `src/test/java/org/fitnesse/cucumber/stepdefinitions`

## What is next
We plan to have a dedicated demo project on how structure it, package it, deploy it and tie it to the pipeline. Stay tuned.


[//]:#

   [FitNesse]: <http://docs.fitnesse.org/FrontPage>
   [Cucumber]: <https://cucumber.io/>
   
