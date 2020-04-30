### Background

The whole project is implemented in IntelliJ and Windows 10 OS. We hope you can test our codes in Windows 10 OS with IntelliJ, because IntelliJ has automatically replace something in initial codes.  Such as, replacing `KThread` to `nachos.threads.KThread`. If you meet problems, please contact us. 

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

In `Condition2.sleep` , we first disabled interrupt (which will be restored at end of `sleep`), and add `currentThread` to `waitqueue`, then release the `lock` and go to `sleep` . When it is woken up, it must immediately acquire the `lock`, and end the `sleep` function. 

In `Condition2.wake`, we also first disabled interrupt, and restore it at end of `wake`. Then if the waiting queue is not empty, we wake up the first thread in it by call its `ready` function, then remove the thread. 

In `Condition2.wakeAll`, we just wake them one by one until the waiting queue is empty. 



### Self Test of Condition2 

We provide a function `selfTest_condition2` in `KThread` Class. You can call it directly in `KThread.selfTest`.

We create two threads sharing a same conditional variable. And the first one will call `conditon2.sleep()` at the beginning, and call `condition2.wake()` and the end. the second thread will call `condition2.wake()` at the beginning, and call `condition2.sleep()` at the end. Then `thread 1` will go to sleep first, and `thread 2` will wake up `thread ` then go to `sleep`, then `thread 1` wake up `thread 2` and end its run, then `thread 2` end. The corresponding output should be printed. 

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

When one calls `Communicator.speak` , it first checks if there exists a waiting listener. If exists, then it speak a word to a shared queue, then wake one listener, and release the lock. 

Otherwise, it goes to sleep, and waits for a listener to wake it up. 

When one calls `Communicator.listen`, it first checks if there exists a waiting speaker. If exists, then they make pair, the listener wake a speaker up, then go to sleep in order to release the lock. Then the speaker will get the lock, offer a word to the shared queue, and wake a listener up.

Otherwise, it just goes to sleep, and waits until a speaker to wake it up.



### Self Test of Communicator

We provide a function `selfTest_Communicator` in `KThread` Class, You can call it directly in `KThread.selfTest`.

In this function, we create $4$ threads, the first two will speak two different words, and the last two will listen. 

Every time the threads call the functions in `Communicator` or do other things, the corresponding message will be printed out. 

At last, the two listener threads will receive different messages. 



### Priority Scheduler

We first notice that, one thread may hold more than one waiting queues, so we can restore them by a list `holdQueues`. But it must be in at most one waiting queue, we can call this `waitQueue`.  For each waiting queue, we must use an variable `lockholder` to restore current lock holder of this waiting queue. 

Thus when we call `getEffectivePriority`, we can update all influenced `effectivePriority` by recursively call `this.waitQueue.lockholder.getEffectivePriority`

When we call `waitForAccess`, we must update the effective priority of current lock holder and its `waitQueue.lockholder`'s effective priority and so on. 

We use an auto incremented variable `cnt` in each waiting queue to make sure FIFO property when priorities are same. The thread with larger `cnt` means later entering time. 

Notice that we need a data structure like heap to implement modification. In order to make it simpler, for each priority, we use a Tree set to maintain waiting threads with this priority. 



### Self Test of Priority Scheduler

We provide two functions in `KThread` Class, one is `selftest_PriorityScheduler`. You can directly call it in `KThread.selfTest()`

In `selftest_PriorityScheduler`, we create 4 threads, and give them priority `2, 7, 3, 4` in order. And thread 3 joins thread 1. The finish order should be thread 2, thread 4, thread 1, thread 3. So we also tested the `join` function. 

Notice that if you want to use Priority Scheduler, you should modify the `nachos.conf`, and let `ThreadedKernel.scheduler = nachos.threads.PriorityScheduler `

We also tested this priority Scheduler in `Boat`, which will be mentioned later.



### Boat

We use some shared variables to finish this task. 

We use variable `is_adult_go` to show if in the next turn a adult needs to go to Molokai, use variable `boat_in_Oahu` to show if the boat is in Oahu currently.  And we also use a variable `is_end` to show if all are on Molokai. To differ the rowing child and the riding child, we need another variable `is_pilot` . 

We also use three condition variables to restore waiting adult on Oahu, waiting child on Oahu, and waiting child on Molokai, three variables to show the number of adults on Oahu, children on Oahu and children on Molokai.

When an adult is forked, it firstly acquire the lock, repeat checking  if `is_adult_go` is true and the boat is in Oahu. Otherwise it sleeps. 

When it finally wakes up, it immediately rows to Molokai, decreasing the number of adults in Oahu. Set `is_adult_go` to be false (The reason is that we want to implement a cycle: two children go to Molokai, one back to Oahu, and one adult go to Molokai, the other child back to Oahu. In this way we can let all adults go to Molokai, then we only need to consider children, which is very easy). And set `boat_in_Oahu` to be false. Then it wakes up one child on Molokai, releases the lock and finishes its thread. 

When a child is forked. It firstly acquire the lock, and create a private variable `my_oahu` to show if this child is on Oahu. Then it repeats checking if `is_end`. If not, then if it is on Oahu, it must consider to go to Molokai. If boat is in Oahu and not `is_adult_go`, then it can go to Molokai. If `is_pilot`, this means it must row to Molokai, and set `is_pilot` be false, then wake up another child to follow him, and he then can sleep on Molokai. Otherwise, it must set `is_pilot` to be true in order to make sure the next children boat from Oahu to Molokai contains one pilot. When it reaches Molokai, it must check if no one remains on Oahu. If so, he can set `is_end = true`, and finish his work. Otherwise, it must wake up one child on Molokai to send the boat back to Oahu. Then it goes to sleep. 

If the child is on Molokai, it must send the boat back to Oahu. After reaching Oahu, it must check if there exists one adult on Oahu. If so, the child must wake up one of them, and let it go to Molokai, otherwise the child can wake up another child to row the boat to Molokai. 

The `Begin` function must not end before `is_end` is true. 



### Self Test of Boat

We just call `Boat.selfTest` in `ThreadedKernel.selfTest`.

We tried to replace the `Condition` with `Condition2` , which did not effect the answer. So we think our `Condition2` is correct. 

We also tried to use priority Scheduler in `Boat`, which also did not effect the answer. So we think our `PriorityScheduler` is also correct.
