There are 4 subdirectories:

doc

db	this includes the schema SQL file

src	contains directories edu/uga/dawgtrades and in dawgtrades the following can be found:
		model contains the interfaces of our entity classes
		model/impl contains the implementation of the entity classes
		persistence contians the interfaces of the Persistence module's API
		persistence/impl contains the implementation of the Persistence module's API

classes
	includes the compiled Java class files

An ant build file is also included (build.xml).

To compile the dawgtrades Object Model module and the test progrms:
	ant compile
It is also possible to compile the example using javac directly.
You should see no compilation errors.

To run the test programs:
	export CLASSPATH=classes:/opt/classes/mysql-connector-java-5.1.26-bin.jar
	(This step is not necessary but if you do not then you must follow java
	 with -cp classPath before the program to run)
	
	java edu.uga.dawgtrades.test.Test1
		this test case is used to create a few simple objects
	
	java edu.uga.dawgtrades.test.Test2
		this test case is used to check the isClosed method of
		auctions and to create simple bids

	java edu.uga.dawgtrades.test.ComplexTest
		this test case should be used after creating the tables
		in the database to populate the tables with atleast one
		of every object. It should be run before every Service
		Test

	java edu.uga.dawgtrades.test.AdministratorServicesTest
		this test case is used to check the methods of the
		administrator

	java edu.uga.dawgtrades.test.AdditionalReadTest
		this test case is used to check other associations between
		classes that are not checked in other reading use cases

	java edu.uga.dawgtrades.test.GeneralServicesTest
		this test case is used to check the methods available to
		everyone

	java edu.uga.dawgtrades.test.RegisteredUserServiceTest
		this test case is used to check the methods available to
		registered users

	java edu.uga.dawgtrades.test.TimerServicesTest
		this test case is used to check the methods called on by
		the timer and should be run after a RegisteredUserService
		Test

	java edu.uga.dawgtrades.test.TotalReadTest
		Reads auctions, categories, items, and registered users

	java edu.uga.dawgtrades.test.TotalDeleteTest
		deletes all objects in the database by deleting auctions,
		categories, items, and registered users and cascading

	java edu.uga.dawgtrades.test.DetailedReadTest
		reads objects from the database and tests associations

recommendation:
For clearest results, access the database and use the command 
source ./db/dawgtrades.sql
Then, run the ComplexTest to create objects.
Test with a ServiceTest
Use the DetailedReadTest to see the results in the database
Repeat(Except for TimerServices which should be run after
	RegisteredUserServices)

   After each of the above commands, the mysql client may be used to list the content of the
  nine tables to see how the programs affect the database tables.

After running the program the class files will be stored in the class subdirectory.
To remove the class files use the following command:
	ant clean
