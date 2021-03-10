# FreshDesktopBackImage

#### 介绍
一个可以在桌面背景图片上添加note的程序

#### 环境
1.  jdk 14
2.  win10

#### 需求
1.  日常有事情，想要记录下来，会写在笔记之类的文档里，方便记忆，但是不直观。
想到用note的方式直接放在桌面背景图片上，相对直观;
2.  可以在文档上按日期记录好计划的活动，桌面会自动根据不同的日期加载相应的
活动内容；
3.  系统启动后，会自动将今天之后的活动记录在桌面上，暂时一天只能记录一次
活动
#### 实现 
1.  对需求1，使用操作图片的api，比如 BufferImage，ImageIO
2.  对需求2，需要一个活动文档，所以未来的活动需要添加，之前的活动需要关闭，
需要操作文档，使用 FileChannel，RandomAccessFile
3.  对需求3，使用 bat，vbe 写启动脚本程序，最好静默启动

#### 技术
1.  JNA
2.  BufferImage
3.  ImageIO
4.  RandomAccessFile
5.  FileChannel
6.  bat
7.  vbe

#### 异味
1.  RandomAccessFile

活动文档里针对已读的标记是4个固定长度预留空白的，因为RW方式，写会覆盖之后的字符，
如果要做到自适应长度，目前我想到的方式是，更新当前活动标志，重写文档。

####  maven
1.  JNA
```POM
<dependency>
    <groupId>net.java.dev.jna</groupId>
    <artifactId>jna</artifactId>
    <version>3.5.1</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>net.java.dev.jna</groupId>
    <artifactId>platform</artifactId>
    <version>3.5.1</version>
    <scope>compile</scope>
</dependency>
```
2.   jdk14编译、打包
```POM
<plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <source>14</source>
        <target>14</target>
    </configuration>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>2.6</version>
    <configuration>
        <archive>
            <manifest>
                <addClasspath>true</addClasspath>
                <classpathPrefix>lib/</classpathPrefix>
                <mainClass>com.bitfly.image.tip.TipImage</mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>2.10</version>
    <executions>
        <execution>
            <id>copy-dependencies</id>
            <phase>package</phase>
            <goals>
                <goal>copy-dependencies</goal>
            </goals>
            <configuration>
                <outputDirectory>${project.build.directory}/lib</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```
    
#### 参考文档
https://blog.csdn.net/qq_36828207/article/details/78784461
    
    