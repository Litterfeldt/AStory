#AStory for Android

A native Android application providing long depraved access to audiobook content. 
It's easy to use and easy to setup, also easy to modify and redistribute. 
Please read the license and feel free to contribute. 

##Background

As a person I love books, they are doorways to other dimensions and other worlds.
Being a contemporary youth; always on the go, I found it hard to find time or focus for reading.
This introduced me to audio books but I soon discovered that Android had poor support for these.
There were some software available but they looked poorly or functioned poorly. The solution, as
always, was to write something to fill that gap. And here we are.

##Dependencies

* A computer to compile the code ( duh.. )
* Android SDK ( > version 17 )
* A phone running (at least) IceCream Sandwich

##Get up and running
Provided that you have all the dependencies installed it's just a matter of typing

`ant release` or `ant debug` in your terminal of choice within the folder and ant does the rest for you. 

This will create an apk in the projects `bin/` folder that you then can transfer and install on any android device that meets above stated criteria.

##Features
As of now it's a very basic audiobook player that does just that. Every audiofile you place in your `ExternalStorage/Audiobooks` folder will be parsed by AStorys super ( not so secret.. ) algoritm and placed inside the library.
(AStory will create the /Audiobooks folder if there isn't one there already) 
When it comes to thumbnails AStory defaults to grabbing the thumbnail from the audiotracks but can be overridden with custom artwork by placing an image file into the book's folder. 

Playback is smooth and resource efficient and is handled in the background by a service which also handle notifications.
Playback is automatically stopped in the event of a phone call en resumed at the end of it. 


##What does not work
* There are no bookmarks
* Algorithm for populating the library is slow and needs some looking over
* The booklist truncates the last list-item, unknown reason (way to lazy to fix that right now) 

##Coming release
* Fix booklist (must stop being so lazy)
* Improved algorithm for populating book library
* Improve overall performance (It's good but could be great)
* Improved notifications with playback control
* Playback control via headset button(s)
* Overall crash fixes 
* Clean up and refactor code
