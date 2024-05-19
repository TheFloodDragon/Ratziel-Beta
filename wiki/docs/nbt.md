---
title: NBT表达式
sidebar_position: 10
---

# NBT表达式

> 在对物品的**NBT标签**进行操作时, 就会用到**NBT表达式**

:::info
自**Minecraft 1.20.5**后, `NBT标签`改为了`物品堆叠组件`
:::

**NBT表达式**则分为两种:

+ NBT节点表达式

+ NBT值表达式

## 相关链接:

+ [NBT格式](https://zh.minecraft.wiki/w/NBT%E6%A0%BC%E5%BC%8F)

+ [物品堆叠组件(1.20.5+)](https://zh.minecraft.wiki/w/%E7%89%A9%E5%93%81%E5%A0%86%E5%8F%A0%E7%BB%84%E4%BB%B6)

## NBT节点表达式

> NBT的节点其实就是指定*在哪修改*

**NBT节点表达式**用来对 **复合类型(NBTCompound)** 或者 **列表类型(NBTList)** 进行编辑

### 形式

**浅层节点:** `节点名`

形如 `Damage` `CustomModelData` 的是浅层节点，也就是只有一层。

**深层节点:** `节点I.节点II`

形如 `display.Name` `display.Name` 的是深层节点。

深层节点有很多层，每层之间以 `.` 分割。

**列表元素节点:** `节点名[0]`

形如 `TestList[0]` `Enchantments[1]` 的是列表元素节点。

节点名末尾以 `[数字]` 的形式，其中数字代表的是该列表中的元素索引。

若有一个名为 `AWA` 的列表 `[ "a", "b", "c" ]`

则 `AWA[0]` 表示列表中的第一个元素 `a` (*没错，索引是从0开始的*)

### 实战

假定有这样一串NBT(复合类型展开):

```YAML
Vacation:
  Duration:
    - days: "5~7"
    - from: "2024/1/5"
      to: "2024/1/7"
```

其中 `Vacation` 就是节点名，因为上面没有别的节点了，所以它是所有节点里最大的，是浅层节点。

而 `Duration` 则不一样，它隶属于 `Vacation`，是深层节点，在第二层位置，表示为 `Vacation.Duration`

`Duration` 列表的第一个元素是 `days: "5~7"`，他是**复合类型**的，索引位置是 **0** ，

引用它，就需要这样的表达式 `Vacation.Duration[0]`

再看 `Duration` 的第二个元素也是**复合类型**的，

要引用它的 `from` 或 `to` ，我们需要使用 `Vacation.Duration[1].from`或者 `Vacation.Duration[1].to`

我们可以分别得到 `"2024/1/5"` 和 `"2024/1/7"`

## NBT值表达式

> NBT的值其实就是**修改成什么**

**NBT值表达式**用来表示的是一种**NBT类型**

### 形式

<h6>一种是直接写</h6>

如 `爱死你了` ，会被解析成 `NBTString` 类型

再如 `114514` ， 会被解析成 `NBTInt` 类型

<h6>另一种是精确写法(精确指定类型)</h6>

形式为 `{V};{T}` ，其中`{V}`代表值，`{T}`代表**类型代号**，具体详见下文**值的类型**

对于集合类型，需要用 `,` 分割集合中的元素，如 `1,1,4,5,1,4ba`，其中 `ba` 是**集合类型代号**，表示 `NBTByteArray`

对于特殊类型，如下:

复合类型表达式: `{ "name": "帅哥",items: [ "小贱剑","好吃的" ] };compound` `{};cpd`

列表类型表达式: `[ "小贱剑","好吃的" ];list` `[];list`

遵循**Json格式**,内部内容会自动解析成**NBT对象**。

:::warning

**列表类型**中的的元素如果数据类型不同，则会无法被正常转换。

例如，列表`[0, 1, 2]`、`[3.14, 2.5]`可以被成功转换，

而`[1, 2.5, 66]`、`["阿巴阿巴", 666]`、`[0.0, 0.1]`无法被正常转换。

若违反此规则，可能会导致**玩家数据丢失**等后果！

:::

### NBT类型

|      类型      |            代号             |   描述   |
|:------------:|:-------------------------:|:------:|
| NBTCompound  |   compound, c, cpd, tag   |  复合标签  |
|   NBTList    |      list, a, array       |   列表   |
| NBTIntArray  |  int_array, intArray, ia  |  整型数组  |
| NBTByteArray | byte_array, byteArray, ba | 字节型数组  |
| NBTLongArray | long_array, longArray, la | 长整型数组  |
|  NBTString   |         string, t         |  字符串   |
|    NBTInt    |          int, i           |   整形   |
|  NBTDouble   |         double, d         | 双精度浮点数 |
|   NBTFloat   |         float, f          | 单精度浮点数 |
|   NBTByte    |          byte, b          |  字节型   |
|   NBTLong    |          long, l          |  长整型   |
|   NBTShort   |         short, s          |  短整型   |