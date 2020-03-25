这是杰哥和柯润和一个傻逼的操作系统课程作业。

### 用法

linux下的话，参考 https://www.cnblogs.com/vincent-zhu/p/10999414.html 

Windows下，我用的是IntelliJ（编译原理用过的那个java开发IDE），把这个git clone下去，然后右键这个文件夹open Folder as IntelliJ project。进了IntelliJ之后，machine文件夹里面的Machine.java是主程序，右键点击他可以看到一个Run。这个就是运行这次project的方法。

project1 主要改动的是threads文件夹里面的，其中KThreads是project1的核心。KThreads.java里面的selfTest函数是每一次你run了Machine.java之后会运行的函数。里面包含了我写（嫖）的这次6个任务的自测函数（你们一看就能看懂）。



### TODO

现在的Condition2貌似有问题，不过只测Condition2发现不了问题，我现在出问题都是用Condition2替换掉了Boat里面的Condition之后，跑Boat的selfTest，然后就挂了。

其余的我没发现什么问题。感觉写的也蛮对的。

现在我们只有代码，别的要交的东西一个都没有QAQ
