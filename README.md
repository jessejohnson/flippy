Flippy, for closer communities

The project is an android application called Flippy.
The application has a logo in the folder

How the applcation Works::::::::::::::::

The application requires a user sign up. 
The user then chooses a community which he belongs to, after which he can subscribe to channels in that community.
When a user subscribe to the channel,  the user receives updates of posts and notices that ate posted in that
channel. Finally the user can unsubscribe to a channel after subscription

Any user can create a channel and as well any user can belong to any channel.
A channel has at most five administrators who have the right to manage the channel. 
An administrator to a channel can create notices an post in a channel.




Packages:::::::::::::::::::::::::
This application has been modularised into packages

1. app
2. core
3. adapter
4. persistence
5. util
6. services
7. profile

Libraries Used in the app
This app uses limited librries as much as possible trying to keep the size small as possible.
But some libraries used include the following ..
1. Ion network library
2. Android support V 4
3. Crouton messaging library
4. Super toast library
5. Android assync library
6. Volley networking library
7. Round imageview library
8. Facebook sdk
9. Google progress bar
10. Ormlite library
11. Parse sdk 1.5

The "app" package

This package handles the user sign up and login, it also handles the social media login options 
and also the spalsh and the walkthrough. The community selection activity also is in this activity.

This package has several classes in it:

The splashActivity:---- 
In this activity several choices and checks are made before the app finally starts.
This also introduces the theme of the app and leads in the introduction to the app name
and tagline



The Walkthrough activity:----
This activity introduces the app using android view pager class.
It uses android fragement with a textview to educate the user on the app.
It also makes use of shapes for indicators.
Future works will be to use libraries to to do the indicating work


The SignUpOption activitty:----
It presents the user with various registeration options to the app
Currently making use of facebook and user email sign-up options only


The Register activity:----
This section presents the user with the needed input fields for the user
to register. Currently using ...
Email address, First name, Lastname and Password.
The user checks are done using the ion network calls


The OnBoardingActivity:...
This activity together with the onboardingTextFragment is a point of familiarization to the app.
It also introduces the app theme. User can catch a glimpse of the features of the Flippy app 
in this activity.

FacebookSignInFragment:...
It handles user connections with his or her social media platform.
The heavy lifting and all the required things are handle by facebook sdk and the result intent
is received and data is processed and sent to the server


The Util package



This package handles all the utility functions that are common to all the other packages.
It contains the applcation class, some service files and also the validator class.

The Flippy application file
This Flippy.java file, declares the parse initialization. 









