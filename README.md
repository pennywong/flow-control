# flow-control
流量控制器

## 实现原理

使用了令牌桶算法：每隔一段时间生成一个令牌，能拿到令牌即可发送

具体算法描述参考：

[令牌桶算法](http://baike.baidu.com/link?url=tx97Fu9eQgjeOy1BNnxHpTraAy2qMugo-3HeO6doXGLhIyuWl-HihzTUPX4u-DSjP8jpIg-RHXJGomi7bcc2cK)

[令牌桶算法和漏桶算法以及流量控制浅谈](http://iamzhongyong.iteye.com/blog/1982113)

## 信号量改进

JDK中的Semaphore有个问题，没有对容量做限制，通过release方法，可以增加permits的数量

``` java
Semaphore semaphore = new Semaphore(1);
semaphore.release();
System.out.println(semaphore.availablePermits());
// 输出为2，信号量变多了
```

实现了一个MaximumLimitedSemaphore，限制信号量的数量最多只能是一开始设定的容量数

``` java
protected final boolean tryReleaseShared(int releases) {
	for (; ; ) {
		int current = getState();
		int next = current + releases;

		if (next < current) // overflow
			throw new Error("Maximum permit count exceeded");

		// 如果释放的信号量超过容量，则设定为容量的大小
		if (next > capacity)
			next = capacity;

		if (compareAndSetState(current, next))
			return true;
    }
}
```
