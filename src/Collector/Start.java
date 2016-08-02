package Collector;

import Collector.EyeTest;
import Collector.RS232Protocol;

@SuppressWarnings("unused")
public class Start{

	
	public Start() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EyeThread thread1 = new EyeThread ();
		thread1.start();
		
		RS232Protocol thread2 = new RS232Protocol ();
		thread2.setDaemon(true);
		thread2.run();
	}

}
