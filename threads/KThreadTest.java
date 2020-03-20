package threads;

public class KThreadTest {

    public KThreadTest(){

    }
    public static void simpleJoinTest(){

        nachos.threads.KThread A_thread = new nachos.threads.KThread(new KThreadTest.A_thread(5));

        nachos.threads.KThread B_thread = new nachos.threads.KThread(new KThreadTest.B_thread(A_thread));

        B_thread.fork();

        B_thread.join();

    }

    public static class B_thread implements Runnable{

        B_thread(nachos.threads.KThread joinee){

            this.joinee=joinee;

        }

        public void run(){

            System.out.println("B is ready");

            System.out.println("forking and joining A...");

            this.joinee.fork();

            this.joinee.join();

            System.out.println("B is end");

        }

        private nachos.threads.KThread joinee;

    }

    public static class A_thread implements Runnable{

        A_thread(int num){

            this.num = num;

        }

        public void run(){

            System.out.println("A is ready");

            System.out.println("A is going on");



            for(int i=0;i<this.num;i++){

                System.out.println("A loops"+i+"times");

                nachos.threads.KThread.currentThread().yield();

            }

            System.out.println("A is end");

        }

        private int num;

    }

}