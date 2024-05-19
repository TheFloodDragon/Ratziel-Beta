---
title: 基础
sidebar_position: 2
---

# 数据结构与类型

## 对象 (Object)

表示以键值对 **（key: value）`** 形式出现的数据

有以下特性：

- 使用“**冒号+空格**”来分开**键**与**值**
  ```YAML
  # YAML
  key: value
  ```

  ```JSON
  "key": "value"
  ```

- 支持多层嵌套（**用缩进表示层级关系**）
  ```YAML
  # YAML
  key:
    child-key1: value1
    child-key2: value2
  ```

  ```JSON
  "key": {
    "child-key1": "value1",
    "child-key2": "value2",
  }
  ```
- 支持 **流式风格（ Flow style）** 的语法（用花括号包裹，用逗号加空格分隔，类似 JSON）
  ```YAML
  # YAML
  key: { child-key1: value1, child-key2: value2 }
  ```

  ```JSON
  "key": { "child-key1": "value1", "child-key2": "value2" }
  ```

- 使用 **问号“?”** 声明一个复杂对象，允许你使用多个词汇（数组）来组成键
  ```YAML
  # YAML
  ?
    - keypart1
    - keypart2
  :
    - value1
    - value2

## 数组（Sequence）

- 一组以 **区块格式（Block Format）（即“破折号+空格”）** 开头的数据组成一个数组
  ```YAML
  # YAML
  values:
    - value1
    - value2
    - value3
  ```

  ```JSON
  "values": [ "value1", "value2", "value3" ]
  ```

- 同时也支持 **内联格式（Inline Format）** 来表达（用方括号包裹，逗号加空格分隔，类似 JSON）
  ```YAML
  # YAML
  values: [value1, value2, value3]
  ```

  ```JSON
  "values": [ "value1", "value2", "value3" ]
  ```

- 支持多维数组 **（用缩进表示层级关系）**
  ```YAML
  # YAML
  values:
    -
      - value1
      - value2
    -
      - value3
      - value4
  ```

  ```JSON
  "values":[[ "value1", "value2"], ["value3", "value4"]]
  ```

## 字符串（String）

字符串**一般不需要用引号包裹**, 但如果字符串包含**空格或者特殊字符(例如冒号)**，则需要加**引号**

- **双引号“"”** 不会对字符串中转义字符进行转义（即正常处理转义字符）

- **单引号“'”** 会对串中转义字符进行转义（将转义字符转成文本）

```YAML
# YAML
strings:
  - Hello without quote # 不用引号包裹
  - Hello
    world # 拆成多行后会自动在中间添加空格
  - 'Hello with single quotes' # 单引号包裹
  - "Hello with double quotes" # 双引号包裹
  - "I am fine. \u263A" # 使用双引号包裹时支持 Unicode 编码
  - "\x0d\x0a is \r\n" # 使用双引号包裹时还支持 Hex 编码
  - 'He said: "Hello!"' # 单双引号支持嵌套"
```

```JSON
"strings":
[
"Hello without quote",
"Hello world",
"Hello with single quotes",
"Hello with double quotes",
"I am fine. ☺",
"\r\n is \r\n",
"He said: 'Hello!'"
]
```

## 布尔值（Boolean）

- “true”、“True”、“TRUE”、“yes”、“Yes”和“YES”皆为**真**
- “false”、“False”、“FALSE”、“no”、“No”和“NO”皆为**假**

```YAML
# YAML
boolean:
  - true # True、TRUE
  - yes # Yes、YES
  - false # False、FALSE
  - no # No、NO
```

```JSON
"boolean": [true, true, false, false]
```

## 整数（Integer）

> 支持二进制表示

```YAML
# YAML
int:
  - 666
  - 0001_0000 # 二进制表示
```

```JSON
"int": [666, 4096]
```

## 浮点数（Floating Point）

> 支持科学计数法

```YAML
# YAML
float:
  - 3.14
  - 6.8523015e+5 # 使用科学计数法
```

```JSON
"float": [3.14, 685230.15]
```

## 空（Null）

> “null”、“Null”和“~”都是**空**，不指定值默认也是**空**

```YAML
# YAML
nulls:
  - null
  - Null
  - ~
  -
```

## 时间戳（Timestamp）

> YAML 也支持 ISO 8601 格式的时间数据

```YAML
# YAML
date1: 2020-05-26
date2: 2020-05-26T01:00:00+08:00
date3: 2020-05-26T02:00:00.10+08:00
date4: 2020-05-26 03:00:00.10 +8
```

## 标量（Scalars）

> 表示 YAML 中最基本的数据类型

包括：

- 字符串
- 布尔值
- 整数
- 浮点数
- Null
- 时间
- 日期
