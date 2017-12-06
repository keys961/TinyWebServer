# Lab 8 轻量级Web Server工程文件

轻量的HTTP Web Server

本工程使用IntelliJ IDEA编写，JDK版本是1.9，编译及运行时建议版本不低于1.7。

### 目录说明

`./src/`: 源码文件；

`./target/`: 生成的`.class`文件，其它文件都可以删，但是唯独`JAR/`目录不能删，因为它存放了生成JAR包的路径；

`./WebSever.jar`: 生产的可执行文件，请使用`java -jar WebServer.jar`运行，不要把它放到其它目录中去，否则运行会出错；

### 编译及运行

方法1：使用IntelliJ IDEA打开该项目，然后Build & Run；

方法2（不推荐）：使用Maven编译该项目，即`maven compile`。