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

​	-  


