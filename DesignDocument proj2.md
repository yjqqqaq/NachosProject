### File System Calls

Generally, these file system calls `creat, open, read, write, close, unlink` are simply implemented by invoking the functions provided by `ThreadedKernel.fileSystem`. However, we have to be more careful about checking the bounds of indices and file descriptors, and return values related to errors, for example:

```
if (descriptor < 0 || descriptor > 15 || openfile[descriptor] == null))
    return -1; // Out of bounds

int writeCount = openfile[descriptor].write(buf, 0, length);
if (writeCount < length)
	return -1; // Failure occurs when writing
``` 

### Memory Management for Multiprogramming

This part is mainly about page tables. We maintain a `LinkedList` of free pages in `UserKernel`. In `loadSections`, the required 
