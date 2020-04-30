### File System Calls

Generally, these file system calls `creat, open, read, write, close, unlink` are simply implemented by invoking the functions provided by `ThreadedKernel.fileSystem`. However, we have to be more careful about checking the bounds of indices and file descriptors, and return values related to errors, for example:

```
if (descriptor < 0 || descriptor > 15 || openfile[descriptor] == null))
    return -1; // Out of bounds

int writeCount = openfile[descriptor].write(buf, 0, length);
if (writeCount < length)
	return -1; // Failure occurs when writing
``` 

Every process initializes with `stdin` and `stdout` opened as the file descriptors 0 and 1, respectively.
```
openfile[0] = UserKernel.console.openForReading();
openfile[1] = UserKernel.console.openForWriting();
```

### Memory Management for Multiprogramming

This part is mainly about page tables. In `UserKernel`, we maintain a `LinkedList` of available pages. In `loadSections`, we first check whether there are enough free pages, then make a page table which maps virtual addresses to physical addresses, and mark the physical pages as occupied. In `unloadSections`, the physical pages are marked free again so that we can reuse the memory when the process exits. All operations on virtual memories are converted to operations on the physical memory by the page table. We have to be careful with these address translations so that we skip no address.

### `exec, join, exit`

To implement these 3 system calls, we need to maintain PID, and the PID of its parent and child for each `UserProcess`.

* When `exec` is invoked, we start a new process initialized with a new PID (copy from a increasing counter), set its parent to be the running process, and add the new process to the running process's children.

* When `join` is invoked, we first check whether the PID is indeed a child of the running process, and return -1 if not. Then the process sleeps (using a condition variable) until the child wakes it up when the child `exit`.

* When `exit` is invoked, we close all opened files, wake up its parent, remove itself from the parent's children, invoke `unloadSections`, halt if it is the final process, and finish the thread. 

### Lottery Scheduler

In lottery scheduling, the next thread is selected randomly: each thread gets some lottery tickets, and their chance of winning the next time slot is proportional to the number of their 'effective lottery tickets'. By 'effective' we mean transfer is allowed: a waiting thread donates its tickets to the queue owner.

`LotteryScheduler` extends `PriorityScheduler`, so most codes can be reused. The key changes are in `LotteryThreadState NextThread()`, which randomly picks the next thread to run, and `int getEffectivePriority()`, which calculates the effective priority of each thread.

### Test cases

`test/sleepsort.c` is the test case for the first 3 tasks. It sorts 3 numbers by assign each number to a process which prints the number after time proportion to that number. Then the processes do some file operations and wait for their child by `join`.


