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

### 46. 优先选择Stream中无副作用的函数
1. forEach只适用于展示报告stream的计算结果，不适合参与计算

2. 将stream中的元素放到集合中可以使用Collection中的toList，toSet，toCollection(collectionFactory)
```java
ArrayList<HeartBeatVo> liveAttendanceStats = groups.stream().filter(Objects::nonNull)
                .map(g -> g.lessons().edges().stream().filter(Objects::nonNull)
                        .map(e -> Optional.ofNullable(e.liveAttendanceStats()).orElse(new LinkedList<>()).parallelStream().filter(Objects::nonNull)
                                .map(h -> HeartBeatVo.builder()
                                        .totalNum(h.fragments().lALiveAttendanceStat().totalNum())
                                        .attNum(h.fragments().lALiveAttendanceStat().attNum())
                                        .foregroundNum(h.fragments().lALiveAttendanceStat().foregroundNum())
                                        .notAttNum(h.fragments().lALiveAttendanceStat().notAttNum())
                                        .backgroundNum(h.fragments().lALiveAttendanceStat().backgroundNum())
                                        .timestamp(Long.valueOf(h.fragments().lALiveAttendanceStat().timestamp().toString()))
                                        .build())
                                .collect(Collectors.toCollection(LinkedList::new)))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toCollection(LinkedList::new))
                )
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(HeartBeatVo::getTimestamp))
                .values()
                .stream()
                .map(this::generateHeartbeatByGroup)
                .sorted(Comparator.comparing(HeartBeatVo::getTimestamp))
                .collect(Collectors.toCollection(ArrayList::new));
```

3. 在collection中可以使用toConcurrentMap将其放在线程安全的map中

### 47. Stream要优先使用Collection作为返回类型
1. stream返回的集合一般是Collection，Set，List，Iterable或者数组

2. 不要再内存中保存巨大的有序序列，在Collection中适当的选择ArrayList和HashSet标准集合类型实现

3. 尽量返回集合，如果不能返回集合则返回可迭代的模式，如flatMap(Collection::stream)

### 48. 谨慎使用stream的并行操作
1. java一直走在并发处理的前沿，wait/notify内置对线程的支持，java5引入了java.util.concurrent提供并行集合和执行者框架，
java7引入fork-join包（并行分解高性能包），java8引入了stream

2. **在使用parallel时一定要注意limit操作，当有limit操作时不建议使用parallel，只有当确定元素集合时可以考虑使用parallel,**
**也就是说一定要有一个明确的范围**

3. **除非很明确parallel能带来性能优势的前提先，避免使用parallel，因为它会造成很多问题，并且使用不当还加大了性能开销**

4. 程序中所有并行的stream pipeline都是在一个通用的fork-join池子中，如果其中一个出现异常，会损害到系统中不相关的其他部分