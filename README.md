# DistributedSharedWhiteBoard

A distributed shared whiteboard system including central server and client, based on TCP socket.

这是一个基于TCP socket进行中央服务器与客户端交互的同步共享画板。

## 功能列表

客户端包含以下功能：
- 用户作为房间管理员创建房间
- 用户申请加入房间
- 所有常见绘画功能
- 保存与导入画板
- 房间独立聊天频道
- 房间管理员系统（如用户列表以及踢人系统）

## 如何使用：

Code文件夹中包含服务器端以及客户端源码。

直接双击点击启动JAR文件将会在本地localhost以及程序默认端口运行，仅仅建议测试时使用。

### 正常使用方法：

先启动服务器端：

cmd运行指令 `java –jar server.jar + [运行的IP地址] + [运行端口] + [服务器名字]`

如：`java –jar server.jar localhost 7674 server0`

客户端连接：

cmd运行指令 `java –jar client.jar + [连接的服务器IP地址] + [连接的服务器端口]`

如：`java –jar client.jar localhost 7674`

## Feature List

The client includes the following features:
- Users can create rooms as room administrators
- Users can apply to join rooms
- All common drawing functions
- Save and import whiteboard
- Independent chat channel for each room
- Room management system (e.g., user list and kick-out system)

## How to Use:

The Code folder contains both server-side and client-side source code.

Double-clicking to launch the JAR file will run it on localhost and the program's default port. This is only recommended for testing purposes.

### Normal Usage Method:

Starting the Server:

Run the following command in cmd:

`java -jar server.jar [IP address] [port] [server name]`

Example: `java -jar server.jar localhost 7674 server0`

Connecting the Client:

Run the following command in cmd:

`java -jar client.jar [server IP address] [server port]`

Example: `java -jar client.jar localhost 7674`
