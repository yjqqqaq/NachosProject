### Background

The whole project is implemented in IntelliJ and Windows 10 OS. We hope you can test our codes in Windows 10 OS with IntelliJ, because IntelliJ has automatically replace something in initial codes.  Such as, replacing `KThread` to `nachos.threads.KTread`. If you meet problems, please contact us. 

### Join

To implement the `KThread.join` function, We use a thread queue to restore the thread to join current thread. And when one thread is finished, we need to wake up the waiting threads in thread queue.

We define a new variable : 

`private static nachos.threads.ThreadQueue waitQueue = null `

Initially, a `KThread` will set `waitQueue=ThreadedKernel.scheduler.newThreadQueue(false);`

In `KThread.join()`, we did following modifications:

 - Check this thread is not current thread 

 - Check current thread is not finished

 - Disable interrupt and restore its interrupt status at the end of `join`

 - Use `waitForAccess` and `sleep` .

In `KThread.finish()`, we did following modifications:

 - If there exists one waiting thread, then set it to be ready.

### Self Test of Join

We provide a public function named `selfTest_join` in `KThread` Class. You can call it directly in `KThread.selfTest`

In this function, we first create a PingTest Thread. And then we create another thread, which join the previous thread. Before the `join` begin, it will print a message `This is an output in random position`, which should be printed out at a random position. After the `join` ,this thread will print a message `successful` which should be the last message you can receive.

We also tested our `join` function in the self test of priority scheduler, which will be mentioned later.



### Condition2

In order to maintain the list of waiting threads in this conditional variable, we define a new var 

`private LinkedList<nachos.threads.KThread> waitqueue` in `Condition2` Class.

In `Condition2.sleep` , we first disabled interrupt (which will be restored at end of `sleep`), and add `currentThread` to `waitqueue`, then release the `lock` and go to `sleep` . When it is waked up, it must immediately acquire the `lock`, and end the `sleep` function. 

In `Condition2.wake`, we also first disabled interrupt, and restore it at end of `wake`. Then if the waiting queue is not empty, we wake up the first thread in it by call its `ready` function, then remove woken thread. 

In `Condition2.wakeAll`, we just wake them one by one until the waiting queue is empty. 



### Self Test of Condition2 

we provide a function `selfTest_condition2` in `KThread` Class.  You can call it directly in `KThread.selfTest`

We create two threads sharing a same conditional variable. And the first one will call `conditon2.sleep()` at begin, and call `condition2.wake()` and the end. the second thread will call `condition2.wake()` at begin, and call `condition2.sleep()` at the end. Then `thread 1` will go to sleep first, and `thread 2` will wake up `thread ` then go to `sleep`, then `thread 1` wake up `thread 2` and end its run, then `thread 2` end. The corresponding output should be printed. 

We also tested `Condition2` in `Boat`, which will be mentioned later. 



### Alarm

We use two new `LinkedList` in `Alarm`.class, one is to restore the waiting queue, and one is to restore corresponding waiting time. 

So in `Alarm.timerInterrupt` , we only need to check the peek of waiting list.

In `Alarm.waitUntil`, we find the suitable position to insert a new waiting thread into the list one by one. 



### Self Test of Alarm

We provide a function `selfTest_Alarm` in `KThread` Class. You can call it directly in `KThread.selfTest`

In this function, we create three threads, which will wait 5000, 2000 and 3000 clock ticks.  Before waiting and after waiting they will print current clock ticks.  And when they finish, they will print waiting clocks + `successful `, eg `2000 successful` 



### Communicator 

We use two variables `speakercount` and `listenercount` to restore the number of waiting speakers and listeners, and two conditional variables to restore the waiting listeners and speakers. 

When one call `Communicator.speak` ,  it first check if there exists waiting listener. If exists, then it speak a word to a shared queue, then wake one listener, and release the lock. 

Otherwise, it goes to sleep, wait for a listener to wake it up. 

When one call `Communicator.listen`, it first check if there exists waiting speaker, if exists, then they make pair, the listener wake a speaker up, then go to sleep in order to release the lock. Then the speaker will get the lock, and offer a word to the shared queue, and wake a listener up.

Otherwise, it just go to sleep, wait until a speaker to wake it up.



### Self Test of Communicator

 We provide a function `selfTest_Communicator` in `KThread` Class, You can call it directly in `KThread.selfTest`.

In this function, we create $4$ threads, the first two will speak two different words, and the last two will listen. 

Every time the threads call the functions in `Communicator` or do other things, corresponding message will be printed out. 

At last, the two listener threads will receive different messages. 