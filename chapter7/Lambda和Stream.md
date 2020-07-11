## 第七章：Lambda和Stream

### 42. Lambda优先于匿名类

```
Collections.sort(words, new Comparator<String>(){
    public int compare(String s1, String s2){
        return Integer.compare(s1.length(),s2.length());
    }
});

Collections.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length()))

Collections.sort(words, comparingInt(String::length))
```

1. Lambda表达式是通过类型推断实现的，它隐藏了所有的类型，包括传入和返回类型，因此更加简洁

2. 需要注意的是在使用Lambda表达式时不要使用List等原生类型

3. 需要注意的是Lambda应该尽量不超过三行，且尽量不要尝试序列化一个Lambda

### 43. 调用方法优于Lambda
1. 从java8开始，基本类型包装类型，例如Integer都实现了sum方法

2. 只要调用方法更简洁，清晰则使用调用方法，否则使用Lambda表达式

### 44. 坚持使用标准的函数接口？

### 45. 谨慎使用Stream
1. 一个stream pipeline包含一个源stream，接着0个或者多个中间操作，最后有一个终止操作

2. stream通常是lazy的，直到调用终止时才会开始计算，对于其中不需要的元素将不会被计算，也就是直到使用时才是用

3. stream是顺序执行的，可以调用parallel使之并发运行，但是不建议这么做，可以看第48条

4. stream并不是万金油，stream有时候会使程序可读性变差，因此要综合考虑使用。必要时可以添加一下非stream的代码块

5. stream一般用于以下操作：统一转换元素序列，过滤元素，利用单个操作改变合并元素顺序，分组，将元素集中到一个集合中，特定搜索条件  
这其实是流处理的一种简单版，也符合流本身的一种特定，流入流入对应汇总

