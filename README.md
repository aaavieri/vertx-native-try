## 0、 用GraalVM打包vert.x为原生应用的sample
## 1、 概念介绍
### 1.1、 vert.x是什么？
+ vert.x是一个基于JVM、轻量级、高性能的应用平台，基于Netty全异步通信，并扩展出了很多有用的特性。重点：全异步通信，几乎所有IO相关的操作都处理成了异步通信，性能很好。vert.x的黄金法则就是不要阻塞(Don’t block me!)。
+ 支持多种编程语言，不过绝大多数使用者都是java系的。
+ 性能在techempower评测中处于前列，[查看地址](https://www.techempower.com/benchmarks/#section=data-r20&hw=ph&test=composite&l=zik0vz-sf)
，在最新一期评测中vert.x的综合性能处于java框架第3位
+ [官网地址](https://vertx.io/)

### 1.2、kotlin是什么？
+ 一句话介绍版：它是一门开发语言，它写的代码可以编译成jar文件运行在jvm上，可以与java互相调用；它有类似goroutine的coroutine协程；它还有很多提高开发效率的语法糖。
+ 搞笑介绍版：
    ```
    美人鱼名场面改写（本人原创）
    技术大佬A：尹先生，你好你好。有什么我们可以帮到您的吗？
    我：我接下来要说的，你们千万别害怕。
    技术大佬A：我们是技术大佬，我们什么没见过？你请说。
    我：我，喜欢上了Kotlin
    技术大佬A：Kotiln是哪个妹子？
    我：不是妹子，是一门特别简单特别容易上手而且特别好用的编程语言！
    技术大佬B：画了一杯咖啡
    我：呃，不，比这个写起来简单
    技术大佬B：画了两条蛇
    我：这个又执行效率太低了。
    技术大佬B：画了个红宝石
    我：它有一个牛逼的爹
    技术大佬A伸手接过画纸：画了一只雨燕鸟
    我弹掉纸：Kotiln啊！超好用语法糖用过没，就是那个亲爹是Jetbrains干爹是Google的Kotiln啊，明白吗？
    技术大佬A：明白了，你继续说。
    我：它说我以前用的编程语言很弱，它有data class，open class，object class，lambda表达式，这些都超好用的啊。
    技术大佬A，B：（后仰）
    我：我看了一下技术文档，就疯狂喜欢上它了。简直太好用了，还支持spring全家桶和vert.x框架……
    技术大佬B：（笑）
    我：你笑什么？
    技术大佬B：我想起高兴的事情。
    我：什么高兴的事情？
    技术大佬B：我儿子会写“Hello World”了。（技术大佬A笑）
    我：你又笑什么？
    技术大佬A：我儿子也会写“Hello World”了。
    我：你们儿子，是同一个人？
    技术大佬A、B：对对，（笑），不是，是在同一个兴趣班。
    我：我再重申一遍，我没开玩笑。（AB笑）
    技术大佬A：我们言归正传，您说的Kotiln，开发中好用吗？
    我：他不是好用不好用的问题，它真的是那种很少见的…它很灵活，也很优雅。遗憾的是，时间太急了，没来得及用在生产上……
    技术大佬了：（笑）
    我：你欺人太甚，我忍你很久了！
    技术大佬B：我儿子会写“Hello World”了。
    我：你明明在笑我都没停过。
    技术大佬B：尹先生，我们是受过专业训练的，无论多菜的人，我们都不会笑，除非忍不住。
    技术大佬A：不如这样，尹先生，你先回去，我们研究一个最简单的Demo，等会来给你演示。
    我：好，你们快点，认真一点，最好就用springboot！（出门）
    技术大佬A，B：（大笑）
    我：（探头）
    技术大佬B：尹先生，你有什么要补充吗？
    我：（离开）
    技术大佬A、B：继续笑。
    我：（探头）
    技术大佬A：尹先生？
    我：（无奈离开）
    ```
+ [官网地址](https://kotlinlang.org/)

### 1.3、GraalVM是什么？
+ GraalVM是一个高性能的通用虚拟机，可以运行使用Jvm系语言、JavaScript、Python 3、Ruby、R等语言开发的应用。
+ 性能高，如果是运行在GraalVM上，可以获得比OPENJDK上更高的性能，可参考[官方比较](https://www.graalvm.org/java/advantages/#accelerating-java-performance)，并且GraalVM也是Oracle家的，算是OPENJDK的亲兄弟了。
+ 本文的重点来了，GraalVM甚至可以将Jvm系语言打包成二进制原生应用。提高应用性能，尤其是提高了启动速度，特别适合云原生应用、FaaS等场景。
+ 原生应用打包的局限性：为了减小打包应用的体积，GraalVM并不会把所有的class全部打入应用中，而是抽取需要的部分加入。但是，
  java的反射以及代理是通过class文件分析不出来具体使用了哪些类哪些方法的。所以对于这样的情况，我们需要做一些配置文件，让GraalVM知道哪些类可能会被反射代理等用到，
  将其加入到应用中，同理还有一些类有static代码块，是在打包期(也就是build期)就需要计算出来的，所以也要加入相关配置告诉GraalVM
+ [官网地址](https://www.graalvm.org/)

## 2、原生应用打包
### 2.1、demo内容
+ 一个简单的vert.x的web服务
+ vert.x的mysql数据库操作包vertx-mysql-client
+ vert.x的redis操作包vertx-redis-client
+ Kotlin支持包：vertx-lang-kotlin和vertx-lang-kotlin-coroutines
+ 日志输出工具：自带的java.util.Logging
+ vert.x版本为4.2.1

### 2.2、参考vert.x官方尝试打包(失败)
+ 开始我先尝试用Java而非Kotlin来写代码，也只用到了mysql和redis，暂时没有使用日志。(重点！后面会有用到Kotlin和日志的坑的)
+ vert.x官网有一篇文章指导如何打包成原生应用，参考[地址](https://how-to.vertx.io/graal-native-image-howto/)
，原理上大概是这样，但是实际操作过程中发现按这样打不出来。
  不停地报一堆类"should be initialized at run time got initialized during image building"
+ 天无绝人之路，在github上找到这篇文章对应的[源码](https://github.com/vertx-howtos/graal-native-image-howto)
。然后、但是，克隆下来之后发现仍然是一样的问题。
  看来不是自己操作的问题，应该是这篇howto太老了？

### 2.3、阅读GraalVM官方文档打包(成功)
+ vert.x的官方文档有问题，那就从GraalVM开始，来了解如何打包原生应用吧，学会了就不只是局限于vert.x打包了，而是放之四海而皆准。
  先了解了一下打包要用的相关配置文件，以及其作用，参考[文档](https://www.graalvm.org/reference-manual/native-image/BuildConfiguration/)
。
+ 看完之后，发现觉得是vert.x官方文档过度优化了，配置了一堆initialize-at-build-time的类，加上一堆initialize-at-run-time的类，这些配置就是之前出现的错误
  "should be initialized at run time got initialized during image building"的原因。
+ 直接把howto上的配置文件native-image.properties文件中的initialize-at-build-time和initialize-at-run-time的的类全部删完，然后再尝试打包，报错，
  把提示错误的类加入配置，再打包，再报错，再加。。。最后加了3个类到initialize-at-run-time的配置下面就OK了
+ 注意：要把自己通过反射启动的Verticle加入reflect-config.json中。

### 2.4、从Java迁移到Kotlin的坑
+ IDEA有个功能，直接右键点一个类，然后选择【Convert Java file to Kotlin file】即可，转换后的程序居然可以直接用，真是大赞
+ 然而我高兴得太早了，vert.x全是Future式的异步写法，很是反人类，我寻思既然用了Kotlin了，为什么不用coroutine来换成同步写法呢，正好vert.x提供了完整
  支持Kotlin的coroutine的API，真是太赞了
+ 写完，打包成原生应用，打包成功，然而一执行，NoClassDef...coroutine的启动居然用到了kotlin.random.Random类，匪夷所思，把这个类添加进
  GraalVM打包的配置文件reflect-config.json中，然后又有新的Kotlin类找不到，继续加吧。还好目前就遇到两个，以后使用了更多Kotlin的原生类的时候可能会加更多，
  谁知道呢。总算搞定了。

### 2.5、加入日志输出工具的坑
+ vert.x默认是用的logback+SLF4J输出日志，这在以jar包形式运行的时候没有任何问题，很完美。但是加入之后尝试打包成原生应用就不停报错了，
  当然报错也还是我们的老熟人，哦不，老熟错：一堆类"should be initialized at run time got initialized during image building"
+ 当然我心想日志输出嘛，能有多少依赖，我一个一个类加到配置里面呗，没想到按下左边，右边又出错，如此循环往复，配置越加越多，也有越来越多的类发生了我们的老熟错。
+ 我放弃了，但是GraalVM的官网又给了我希望。不知道是不是GraalVM的开发者也试过用各种日志组件出错，总之它的官方文档里面专门有
[一篇](https://www.graalvm.org/reference-manual/native-image/Logging/)
来介绍怎么在原生主应用中输出日志的。是的，它推荐使用java.util.Logging，
  java自带的日志输出工具，仿佛让我又回到了梦开始的地方
+ 按官方文档的说法，Logger的初始化既可以Build-Time，也可以在Runtime，我选择了在Build-Time，找了个地方写了一段static代码来初始化java.util.Logging。
    ```kotlin
    object Starter {
        @JvmStatic
        fun main(args: Array<String>) {
            Launcher.main(args)
        }

        init {
            try {
                LogManager.getLogManager().readConfiguration(Starter::class.java.getResourceAsStream("/logging.properties"))
                val logger = Logger.getLogger(Starter::class.java.name)
                logger.info("logger init completed")
            } catch (e: IOException) {
                e.printStackTrace()
                exitProcess(0)
            }
        }
    }
    ```
+ 注意：
  * 用到的配置文件logging.properties，要添加到GraalVM打包的配置文件resource-config.json中方可使用
  * 根据GraalVM的官方文档中提到的，要把java.util.logging.FileHandler类添加到配置文件reflect-config.json中。
  * 我没有使用vert.x自身的Launcher类来运行main函数，而是重写了一个Starter类来调用它，主要目的是保证static代码段最早执行，其实似乎是没有必要的。

### 2.6、demo其它说明
+ [github地址](https://github.com/aaavieri/vertx-native-try.git)
+ 组件版本：
  |组件名|版本|
  |--|--|
  |GraalVM|20.3.0|
  |vert.x|4.2.1|
  |Kotlin|1.5.10|

+ 打包成fat-jar：mvn clean package
+ 打包成原生应用：mvn clean package -f pom-native.xml
+ 注意，打包成原生应用windows可能不行，因为除了GraalVM之外，还需要额外安装C++编译器，GraalVM官方说安装VS 2017 C++ builder即可，但是我试过不行，
  幸好我还有一台Macbook
+ 执行fat-jar：java -jar ./target/vertx-native-try-1.0.0-SNAPSHOT-fat.jar run cn.yjl.vertx.MainVerticle -conf ./conf/application.json
+ 执行原生应用：./target/vertx-native-try run cn.yjl.vertx.MainVerticle -conf ./conf/application.json
