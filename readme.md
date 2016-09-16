CS441 @ UIC: HOMEWORK1
====================
Developed by Marco Arnaboldi (marnab2@uic.edu)

Description
--------------------
Your client will create a worksheet whose name is DDMMYYYY, where DD stands for a given day, MM for the month, and YYYY is for the year. Your client will listen to your Gmail account and will check to see if new messages arrived. When the client detects new messages, it extracts the time of their delivery and the subjects of the newly delivered message and it insert this information into the corresponding cells in the worksheet for the given day/month/year. You can complete this homework using either Java (I prefer you to use Java for this first assignment) or Go or Clojure or Scala and you will use Gradle for building the project and running automated tests. You will use the latest community version of IntelliJ IDE for this assignment.

Development & Design choices
-----------------
The application exploits Google APIs in order to achieve the requirements. In particular the following APIs ant their RPC were used:

+ **Gmail**: provides access to an user mails. Access can be granted at different levels, in this application maximum level of privileges is requested and acquired over the user's mail service. In this way it's possible to create a subscription to the Gmail service.
+ **Drive**: provides access to an user drive. Access can be granted at different levels, in this application maximum level of privileges is requested and acquired over the user's drive service. In this way is possible to create and modify files belonging to the user.
+ **PubSub**: provides access to a Publisher/Subscriber service. In this way is possible to create a subscription for the client to the desired topic and also to grant to the Gmail service, publisher privileges over the topic. Using this strategy the client is able to query the topic (PULL request) and if something has been published and if to retrive the newest publications. In this case the publisher (i.e. Gmail) will publish update only if new incoming mail are detected.
+ **SpreadSheet**: provides access to an user spreadsheets. Access can be granted at different levels, in this application maximum level of privileges is requested and acquired over the user's spreadsheet service. In this way it's possible to modify its content and update it.

The application was developed with IntelliJ, with the use Gradle in order to manage the libraries. It has been designed in order to be as extendable as possible.
In detail, it's composed by 3 modules composed by different classes:

+ **Configuration**: contains classes aimed to manage the access configuration
    + *PortableConfiguration*: this class is in charge to configure and initiate the PubSub client with portable credential stored into the resources
+ **Wrapper**: contains classes working as wrappers
    + *RetryHttpInitializerWrapper*: this class will automatically retry upon RPC failures, preserving the auto-refresh behavior of the Google
+ **Services**: contains classes that generate the connection to the given service and expose in an useful way its RPC
    + *DriveService*: this class is in charge to manage Google Drive RPC. It creates a connection and exposes methods to retrive the list of the spreadsheet in the accredited account and to create a new one if necessary.
    + *MailService*: this class is in charge to manage Google Gmail RPC. It creates a connection and exposes methods to retrive unread messages by today, to retrive a message from a given history information, to start a watcher over the accredited user Gmail account via a pre-settled Google topic
    + *PubSubService*: this class is in charge to manage Google PubSub RPC. It creates a connection and exposes a method to watch over a subscription via PULL request and if a new publication is available it retrieves it and call the correct service to handle it (i.e. in this application the retrieved publication is a message containing the history changeLog  - when a mail comes- of the Gmail service, so the MailService is called in order to retrieve the just income mail)
    + *SpreadsheetService*: this class is in charge to manage Google PubSub RPC. It creates a connection and exposes a method to modify its content

Further information about classes and their methods can be find as comment into the code.

Functionalities
----------------

The application run into a client (e.g. your personal computer). At its start the application, if not exists, creates a file named with the current date DDMMYYYY, then fill it with the unread mails till the current day. After that if a new mail incomes, the application appends its information to the file.

Usage
----------------

To use the application open the terminal and type as the following snippet of code, from the folder where the executable is located:

`java HW1_MarcoArnaboldi`

Press any key to stop it.

Test
----------------
The testing was made using a python script that sends mail to the defined mail account. Usage of the scripts is the following:

Steps for the test:

1. Launch the application
1. Send a mail to the account using the python script
1. Check if the mail was added to the current spreadsheet

To check the results the credential to access the accounts are:

Google Account: marco.arnaboldi91@gmail.com
Password: hh7-JAL-mJX-wBB

Acknowledgments
---------------
Some inspiration was taken by the [Google API Tutorial](https://developers.google.com). The code was rewritten and readapted in order to implement the described functionalities.