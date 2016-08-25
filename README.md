# EyeTribe Collection Application
Multi-Threaded Java Application for EyeTribe Data Collection

# Synopsis:

Multi-threaded java data collection application for the EyeTribe.
This application is used to collect eye movement data for tasks such as pointing and grasping for later analysis.
It includes Arduino files needed for temporal sync with the Eyelink eye-tracker.

# Sync:
1. The Eyelink program (Experiment Builder) can change the TTL level of the status pins of the parallel port on the host computer. The pre-set TTL level is high. When recording is begining, the TTL level changes from high to low, then back to high (inverted pulse). When recording has ended, the TTL level again changes mimicking the inverted pulse.
2. When the Arduino pin 12 changes logic state twice, a '1' is written to the serial buffer. 
3. The collection application has a RS232 protocol daemon thread which monitors for a serial event. When there is a serial event, the recording turns on/off (by changing the 'isStreaming' boolean variable).
4. The daemon thread clears the serial buffer by outputting to the console.

### Sync Setup:
1. Ensure the parallel port cable is plugged into the parallel port of the host computer.
2. Two Arduino wires can be plugged into the parallel port head. They plug into the Arduino (Ground and Pin 12). 
  - The placement of the two wires is written on the parallel port cable.
3. Plug in the Arduino Uno into the collection device (using USB port 3).
  - Verify using the Device Manager.
4. Open Command Prompt and 'cd' into the directory containing the runnable-jar.
  - ex. ``` cd C:\Users\Nisarg\Desktop\Eclipse_Workspace\Leap_Motion_Data_Collector\bin\Data_Collector ```
5. Use the run command to run the application.
  - ```java -jar name-of-application.jar ```

  
# FAQs:

- There is an error appearing when I run the application and the sync is not working.

Confirm that the Arduino is plugged into the correct port (port 3). Also confirm that there are no other programs open and using port 3.
One can change the port being used by the Arduino Uno by going into device manager, and going into 'Advanced Port Settings'.

- There are strings appearing on the Command Prompt during collection.
  
Those are due to the clearing of the serial buffer. The Arduino and colllection application communicate via the serial buffer, and it must be cleared by outputting to the console. It is a byproduct of the sync and does not impede it.

- The EyeTribe is not being recognized by the application.

Before this application can be used, an EyeTribe server must be setup. Run the EyetribeUI program included in the EyeTribe SDK to start the server.
Perform calibration to ensure the validity of the data.
    - The EyeTribe SDK can be downloaded from http://dev.theeyetribe.com/general/ 
    - An EyeTribe account that has a product order history is needed in order to download the SDK
![alt text][logo]

[logo]: https://github.com/nisargbhavsar/EyeTribe_Collector/blob/master/Sample.png "Collection Application"
