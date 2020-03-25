package nachos.threads;
import nachos.ag.BoatGrader;

public class Boat
{
    static BoatGrader bg;
    private static nachos.threads.KThread parentThread;// 父进程
    private static int children_number_Oahu;// 在Oahu岛上孩子的数量
    private static int adult_number_Oahu;// 在Oahu岛上成人的数量
    private static int children_number_Molokai;// 在Molokai岛上孩子的数量
    private static int adult_number_Molokai;// 在Molokai岛上成人的数量
    private static nachos.threads.Condition2 children_condition_Oahu;// 孩子在Oahu岛上的条件变量
    private static nachos.threads.Condition2 children_condition_Molokai;// 孩子在Molokai岛上的条件变量
    private static nachos.threads.Condition2 adult_condition_Oahu;// 成人在Oahu岛上的条件变量

    private static nachos.threads.Lock lock;
    private static boolean is_adult_go;// 判断是否该成人走
    private static boolean boat_in_Oahu;// 判断船是否在Oahu
    private static boolean is_pilot;// 判断现在的孩子 是不是驾驶员
    private static boolean is_end;// 判断运送是否结束

    public static void selfTest()
    {
	BoatGrader b = new BoatGrader();

	// System.out.println("\n ***Testing Boats with only 2 children***");
	// begin(0, 2, b);

//	System.out.println("\n ***Testing Boats with 2 children, 1 adult***");
//  	begin(1, 2, b);

  	System.out.println("\n ***Testing Boats with 10 children, 7 adults***");
  	begin(5, 3, b);
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
        lock = new nachos.threads.Lock();
        children_condition_Oahu = new nachos.threads.Condition2(lock);
        children_condition_Molokai = new nachos.threads.Condition2(lock);
        adult_condition_Oahu = new nachos.threads.Condition2(lock);
        is_pilot = true;
        is_adult_go = false;
        is_end = false;
        boat_in_Oahu = true;

    }


    static void AdultItinerary() {
	    bg.initializeAdult(); //Required for autograder interface. Must be the first thing called.
	//DO NOT PUT ANYTHING ABOVE THIS LINE.
        lock.acquire() ;
        while (!(is_adult_go && boat_in_Oahu)) {
            adult_condition_Oahu.sleep();
            //System.out.println("爷活过来啦1");
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
    	boolean my_oahu = true ;
	    lock.acquire();
	    while (!is_end) {
	        if (my_oahu) {
	            if (!boat_in_Oahu || is_adult_go) {
	                children_condition_Oahu.sleep() ;
                }
	            if (is_pilot) {
	                bg.ChildRowToMolokai();
	                is_pilot = false ;
	                children_number_Oahu -- ;
	                my_oahu = false ;
	                children_condition_Oahu.wake() ;
	                //System.out.println("wo yao shui le ");
	                children_condition_Molokai.sleep() ;
	                //System.out.println("爷活过来啦！");
                }
	            else {
                    bg.ChildRideToMolokai() ;
                    my_oahu = false ;
                    children_number_Oahu -- ;
                    is_pilot = true ;
                    boat_in_Oahu = false ;

	                if (adult_number_Oahu == 0 && children_number_Oahu == 0) {
	                  //  System.out.println("爷晕了");
	                    is_end = true ;
	                    children_condition_Molokai.wakeAll() ;
                    }
	                else {
	                  //  System.out.println("worinige");
	                    children_condition_Molokai.wake() ;
	                    children_condition_Molokai.sleep();
	                    if (adult_number_Oahu > 0) is_adult_go = true ;
                    }
                }
            }
	        else {
	            bg.ChildRowToOahu();
	            my_oahu = true ;
	            children_number_Oahu ++ ;
	            boat_in_Oahu = true ;
	            if (adult_number_Oahu == 0) {
	                is_adult_go = false ;
	                children_condition_Oahu.wake() ;
                }
	            else {
	                if (is_adult_go) adult_condition_Oahu.wake() ;
	                else children_condition_Oahu.wake() ;
                }
	            children_condition_Oahu.sleep() ;
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
