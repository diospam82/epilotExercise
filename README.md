This project implements a little coding exercise for the company ePilot.

Sources were created by Dominik von Lavante in Eclipse with the Spring-Boot Launcher and Maven.

The requirements for this program are as follows:
Write a small Springboot application that interacts with github.com and exposes 2 endpoints:
•	GET - /active/<user> - calling this endpoint will return a json body with a boolean field. 
If the specified user has pushed code in the last 24 hours, it will be true. It will be False otherwise.
•	GET /downwards/<repo> - calling this endpoint will return a json body with a booleam field. 
If the specified git repo had more deletions than additions in the last 7 days, return true. The field will be False otherwise.
Package it as a docker container.
Try to document the functionality to the best of your ability in the README.md.
No github specific library or sdk is allowed. Other third party libraries are allowed.
Testing is encouraged.
Please provide a public git repo to us with the code, Dockerfile and README.md.

Implicit decisions that I derive from these requirements:
-No user authentification allowed -> Application is limited to Github REST API 3.0
-A get query for <repo> only is impossible. Only the combination of an owner + repo may be queried. 
-The API has hence been changed to /downwards/<owner>/<repo>
-It is assumed that the deletions and additions are only to be summed up for succesful pulls that were merged. Open pulls are ignored 
as well as closed, unsuccesfull, pulls.

General structure of code:
For each GET command there is a separate controller class.
"UserActivityController" for the first /active/<user> command
"DownwardsController" for the second /downwards/<owner>/<repo> command
All JSON answers from Github will be stored in the respective classes "UserActivity" and "RepoPullRequest"
The answers delivered by my program for the GET requests are stored in the classes "UserActivityAnswer" and "DownwardsAnswer"
Lastly everything is glued together by the main class "RecruitmentAssignmentApplication"

All query Strings for Github as well as all time limits are configurable via the "Application.Properties" file. 

Some effort for error handling has been done. A proper production application would need a lot more effort in this regard and 
needs to implement the full Spring HTTP error handling process!! 
There is currently no user-input sanitation and validation performed. Again, time is short and a proper production 
application would need a lot more effort in this regard!!
A proper application also needs to be secured and requires authentification via Spring security.



