links:
swagger ui: http://localhost:8080/swagger-ui.html

I chose swagger for create documentation of API because it's easy way to create interface for request
I try to use simple way to create background task, so i souse @Async annotation. i have some problem
with stop this tusk, but use  Future, it have a process  how to do it. but i have som problem with
task state, so i chose sharing simple date with background task and main tread, it's not very good
solution but it fast.
I did not spend much time for DefaultMovieService it can be better.

What i can do better:
1) create myself callable class  and use FutureTask.
2) make multithreaded query for side api, for example use separate task for all call side api, but
we have problem how to stop all of them.
3) spend more time for settings
4) support exception for rest and answer
5) add cash for api calling
