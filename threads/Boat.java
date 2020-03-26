package nachos.threads;
import nachos.ag.BoatGrader;

public class Boat
{
    static BoatGrader bg;
    private static nachos.threads.KThread parentThread;
    private static int children_number_Oahu;
    private static int adult_number_Oahu;
    private static int children_number_Molokai;
    private static nachos.threads.Condition children_condition_Oahu;
    private static nachos.threads.Condition children_condition_Molokai;
    private static nachos.threads.Condition adult_condition_Oahu;

    private static nachos.threads.Lock lock;
    private static boolean is_adult_go;
    private static boolean boat_in_Oahu;
    private static boolean is_pilot;
    private static boolean is_end;

    public static void selfTest()
    {
	BoatGrader b = new BoatGrader();

	// System.out.println("\n ***Testing Boats with only 2 children***");
	// begin(0, 2, b);

//	System.out.println("\n ***Testing Boats with 2 children, 1 adult***");
//  	begin(1, 2, b);

  	System.out.println("\n ***Testing Boats with 10 children, 7 adults***");
  	begin(10, 7, b);
    }

    public static void begin(int adults, int children, BoatGrader b) {

        bg = b;
        parentThread = nachos.threads.KThread.currentThread();

        for (int i = 0; i < adults; i++)
            new nachos.threads.KThread(new Runnable() {
                public void run() {
                    AdultItinerary();
                }
            }).setName("father " + i).fork();

        for (int i = 0; i < children; i++)
            new nachos.threads.KThread(new Runnable() {
                public void run() {
                    ChildItinerary();
                }
            }).setName("child " + i).fork();

        children_number_Oahu = children;
        adult_number_Oahu = adults;
        children_number_Molokai = 0;

        lock = new nachos.threads.Lock();
        children_condition_Oahu = new nachos.threads.Condition(lock);
        children_condition_Molokai = new nachos.threads.Condition(lock);
        adult_condition_Oahu = new nachos.threads.Condition(lock);
        is_pilot = true;
        is_adult_go = false;
        is_end = false;
        boat_in_Oahu = true;


        //while (!is_end) parentThread.yield();
    }


    static void AdultItinerary() {
	    bg.initializeAdult(); //Required for autograder interface. Must be the first thing called.
	//DO NOT PUT ANYTHING ABOVE THIS LINE.
        lock.acquire() ;
        while (!(is_adult_go && boat_in_Oahu)) {
            //System.out.println(nachos.threads.KThread.currentThread().getName() + "go to sleep") ;
            adult_condition_Oahu.sleep();
            //System.out.println(nachos.threads.KThread.currentThread().getName() + "wake up") ;

        }
        bg.AdultRowToMolokai();
        adult_number_Oahu -- ;
       // System.out.println(adult_number_Oahu) ;
        is_adult_go = false ;
        boat_in_Oahu = false ;
        children_condition_Molokai.wake() ;
        lock.release();

	/* This is where you should put your solutions. Make calls
	   to the BoatGrader to show that it is synchronized. For
	   example:
	       bg.AdultRowToMolokai();
	   indicates that an adult has rowed the boat across to Molokai
	*/
    }

    static void ChildItinerary() {
	    bg.initializeChild();
        lock.acquire();
    	boolean my_oahu = true ;
	    while (!is_end) {
	        if (my_oahu) {
	            while (!boat_in_Oahu || is_adult_go) {
	                //System.out.println(nachos.threads.KThread.currentThread().getName() + "go to sleep");
	                children_condition_Oahu.sleep() ;
                }
                children_number_Oahu -- ;
                children_number_Molokai ++;
	            if (is_pilot) {
	                bg.ChildRowToMolokai();
	                is_pilot = false ;
	                my_oahu = false ;
	                //System.out.println("hanren ");
                    //System.out.println(children_number_Oahu) ;
	                children_condition_Oahu.wake() ;
	               // lock.acquire();
	               // System.out.println("wo yao shui le " + nachos.threads.KThread.currentThread().getName());
	                //System.out.println("wo xing le!!!" );

                }
	            else {
                    bg.ChildRideToMolokai() ;
                    my_oahu = false ;
                    is_pilot = true ;
                    //System.out.println(children_number_Oahu);
	                if (adult_number_Oahu == 0 && children_number_Oahu == 0) {
	                    //System.out.println("niubi ");
	                    is_end = true ;
	                    is_adult_go = true ;
                    }
	                else {
                        boat_in_Oahu = false ;
	                    children_condition_Molokai.wake() ;
                    }
                }
                //children_condition_Molokai.sleep();
            }
	        else {
	            //System.out.println("sha bi le ");
                while (boat_in_Oahu) {
                  //  System.out.println(nachos.threads.KThread.currentThread().getName() + "goto sleep");
                    children_condition_Molokai.sleep();

                   // System.out.println(nachos.threads.KThread.currentThread().getName() + "wake up");
                }

	            bg.ChildRowToOahu();
	            my_oahu = true ;
	            children_number_Oahu ++ ;
	            children_number_Molokai -- ;
	            boat_in_Oahu = true ;
	            if (adult_number_Oahu == 0 || children_number_Molokai == 0) {
	                is_adult_go = false ;
	                //
	                children_condition_Oahu.wake();
	                //lock.acquire();
                }
	            else {
	                is_adult_go = true ;
	               // System.out.println(nachos.threads.KThread.currentThread().getName() + "Call for adult");
	                adult_condition_Oahu.wake();
	                //lock.acquire();
                }
	            //children_condition_Oahu.sleep();
            }
        }
        lock.release();
    }

    static void SampleItinerary()
    {
	// Please note that this isn't a valid solution (you can't fit
	// all of them on the boat). Please also note that you may not
	// have a single thread calculate a solution and then just play
	// it back at the autograder -- you will be caught.
	System.out.println("\n ***Everyone piles on the boat and goes to Molokai***");
	bg.AdultRowToMolokai();
	bg.ChildRideToMolokai();
	bg.AdultRideToMolokai();
	bg.ChildRideToMolokai();
    }

}
