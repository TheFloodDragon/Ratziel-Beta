# NBT表达式

> 在对物品的**NBT标签**进行操作时, 就会用到*NBT表达式*

## 简介

NBT表达式分为两种:

+ NBT节点表达式

+ NBT值表达式

顾名思义，对NBT标签的编辑，既需要知道在哪修改，也需要知道修改成什么。

## NBT节点表达式

> NBT的节点其实就是指定*在哪修改*

### 一般形式

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

### 实际判断

假定有这样一串NBT:

```YAML
Vacation:
  Duration:
    - "5 ~ 7"
    - from: "2024/1/5"
      to: "2024/1/7"
```

其中 `Vacation` 就是节点名，因为上面没有别的节点了，所以它是所有节点里最大的，是浅层节点。

而 `Duration` 则不一样，它隶属于 `Vacation`，是深层节点，在第二层位置，表示为 `Vacation.Duration`

再看 `Duration` 其实是个列表，第一个元素是 `"5 ~ 7"`，它的索引位置是 0 ，引用它，就需要这样的表达式 `Vacation.Duration[0]`

而 `Duration` 的第二个元素是复合的，要引用它的 `from` 或 `to` ，我们需要使用 `Vacation.Duration[1].from`
或者 `Vacation.Duration[1].to`

我们可以分别得到 `"2024/1/5"` 和 `"2024/1/7"`

## NBT值表达式

> NBT的值其实就是*修改成什么*

### 一般形式

<h6>一种是直接写</h6>

如 `爱死你了` ，会被解析成 `NBTString` 类型

再如 `114514` ， 会被解析成 `NBTInt` 类型

<h6>另一种是精确写法(精确指定类型)</h6>

精确写法必须以 `:` 开头

一般形式 `:{V}{T}` ，其中`{V}`代表值，`{T}`代表**类型代号**

对于集合类型，需要用 `,` 分割集合中的元素，如 `1,1,4,5,1,4ba`，其中 `ba` 是**集合类型代号**，表示 `NBTByteArray`

对于特殊类型，`:cpd` 表示 `NBTCompound` 类型，`:list` 表示 `NBTList` 类型

示例:

NBTShort - `:666s`
NBTFloat - `:2.333f`
NBTIntArray - `1,2,3,4,5ia`

### 值的类型

**允许以下类型的值**

|      类型      |        代号         |   描述   |
|:------------:|:-----------------:|:------:|
| NBTCompound  | c , cpd, compound |  复合类型  |
|   NBTList    |      l, list      |  列表类型  |
| NBTIntArray  |        ia         |  集合类型  |
| NBTByteArray |        ba         |  集合类型  |
| NBTLongArray |        la         |  集合类型  |
|  NBTString   |         t         |  字符串   |
|    NBTInt    |         i         |   整形   |
|  NBTDouble   |         d         | Double |
|   NBTByte    |         b         |  Byte  |
|   NBTLong    |         l         |  Long  |
|   NBTFloat   |         f         | Float  |
|   NBTShort   |         s         | Short  |