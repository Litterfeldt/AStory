#AStory for Android

The great looking audio book player.

As a person I love books, they are doorways to other dimensions and other worlds. Being a contemporary youth; always on the go, I found it hard to find time or focus for reading. This introduced me to audio books but I soon discovered that Android had poor support for these. There were some software available but they looked poorly or functioned poorly. The solution, as always, was to write something to fill that gap. And here we are.

AStory gives you just that, a story; no fuss, no functionality other than the basics. There are no bookmarks, no ads and no intrusive popups. When you are tired of listening you just dismiss the notification and the app is gone, no overhead and no background services. When you feel like listening again you'll notice that AStory always keeps track of where you left of. 

It functions just as you expect it to and that it looks great to makes everything come together. The design is easy on the eyes and optimal for it's single purpose. 

AStory's design is heavily inspired by the metro design language; some dislike it, some love it. I'm sure that after giving it a try you will notice how nonintrusive the whole concept of the app is and how it fits right into your phone usage patterns. 

####IMPORTANT
When you start AStory for the first time it will create a folder for you on you external storage called "Audiobooks" put your books there and please make sure that they have high quality images embedded, everything looks so much better with some eye-candy.


##Dependencies

* A computer to compile the code ( duh.. )
* Android SDK ( > version 17 )
* A phone running (at least) Android 4.1

##Get up and running
Download the app directly from the [Google Play Store.](https://play.google.com/store/apps/details?id=com.Litterfeldt.AStory)

If you want to build it from source, you can. Provided that you have all the dependencies installed it's just a matter of typing
`ant release` or `ant debug` in your terminal of choice within the folder and ant does the rest for you. 

This will create an apk in the projects `bin/` folder that you then can transfer and install on 
any android device that meets above stated criteria.


##What it doesn't have 
* There are no bookmarks
* Playback control via headset button(s)
* Playback control via notification
* ~~Algorithm for populating the library is slow and needs some looking over~~
* ~~The booklist truncates the last list-item, unknown reason (way to lazy to fix that right now)~~

##Coming release
* Playback control via headset button(s)
* Playback control via notification
* Improved design for notifications

##Last release brought
* Fix booklist (must stop being so lazy)
* Improved algorithm for populating book library
* Improve overall performance (It's good but could be great)
* Improved notifications with playback control
* Overall crash fixes
* Clean up and refactor code
