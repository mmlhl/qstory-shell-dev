# QStory 脚本转换器
## 个人使用的工具
这是一个在idea搭建qstory开发环境的项目。
代码由gork3生成
## 功能
- 将继承 `AbstractQStoryScript` 的 Java 类转换为 `.java` 文件。
- 自动调整 QStory 特定的回调方法（例如 `onMsg`、菜单回调）。
- 将 SDK 方法调用替换为 QStory 全局变量（例如 `getMyUin()` -> `MyUin`）。
- 修正 API 方法名以符合 QStory 规范（例如 `toast` -> `Toast`）。

## 前置条件
- **Java**: 17 或更高版本
- **Maven**: 3.6 或更高版本
- **IntelliJ IDEA**: 可选，用于开发和编辑脚本

## 使用
- 修改src/main/java/org/example/MyQStoryScript.java的文件
- 右键src/main/java/org/example/SmartJavaToBeanShellConverter.java点击运行
- 将文件推送到手机
- 推荐使用idea的自动同步功能加上mt管理器的ftp