package nachos.threads;

import nachos.machine.*;

import java.util.Queue;
import java.util.LinkedList ;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
        lock = new nachos.threads.Lock() ;
        queue = new LinkedList<Integer>() ;
        speakCondition = new nachos.threads.Condition2(lock) ;
        listenCondition = new nachos.threads.Condition2(lock) ;
        word = 0;
        speakercount = 0;
        listenercount = 0 ;
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
        boolean status = nachos.machine.Machine.interrupt().disabled() ;
        lock.acquire() ;
      //  System.out.println("what the fuck") ;
        if (listenercount == 0) {

            speakercount ++ ;
            speakCondition.sleep() ;
            queue.offer(word) ;
            listenCondition.wake() ;
            speakercount -- ;
        }
        else {
            queue.offer(word) ;
            listenCondition.wake() ;
        }
        lock.release();
        nachos.machine.Machine.interrupt().restore(status) ;
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
        boolean status = nachos.machine.Machine.interrupt().disabled() ;
        lock.acquire() ;
       // System.out.println("heihei") ;
        if (speakercount > 0) {
            speakCondition.wake() ;
            listenCondition.sleep() ;
        }
        else {
            listenercount ++ ;
            listenCondition.sleep() ;
            listenercount -- ;
        }
        int answer = queue.poll() ;
        lock.release();
        nachos.machine.Machine.interrupt().restore(status) ;
        return answer ;
    }

    private nachos.threads.Lock lock;

    private nachos.threads.Condition2 speakCondition, listenCondition;

    private int word,speakercount,listenercount;

    private Queue<Integer> queue;
}
