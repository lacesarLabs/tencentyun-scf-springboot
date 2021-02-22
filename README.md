# 基于腾讯云函数部署的springboot
特性：

- [x] **原生springboot调试** - 直接按照原生的springboot进行开发调试
- [x] **本地模拟云函数调用** - 可以在本地模拟API网关-云函数调用流程
- [x] **轻量** - 将核心文件移植到自己的springboot下即可进行云函数部署

#### 本demo基于 springboot-2.2.9.RELEASE 编写
#### 核心文件scf/ScfRoute2.java

# 环境
    jdk 1.8
    maven

# 快速上手

### 1.使用maven命令进行scf项目打包
```
mvn clean package -Pscf
```

### 2.新建云函数
- 自定义创建
- 运行环境Java8
- 本地上传zip包
- 选择target/demo-1.0-scf.jar
- 执行方法填入scf.ScfRouter2::routePath
- 完成

### 3.新建API网关
- 新建一个网关服务
- 进到新建的服务里新建一个通用API
    - 路径配置为/{path}
    - 请求方法选择ANY
    - 参数配置增加参数名path 参数位置选择path
    - 后端配置选择云函数SCF（选择刚刚新建的云函数）
    - 勾选集成响应
    - 完成

### 4.API网关访问地址
- 通过get请求 {{网关地址}}/hello?name=world! 得到响应 hello world!
- 通过post请求 {{网关地址}}/hello (参数:{"name":"world!"}) 得到响应 hello world!

# 打包
1.普通springboot打包
```
mvn clean package -Pspringboot
```
2.腾讯云scf打包
```
mvn clean package -Pscf
```

## 坑
1.创建云函数提示报错"you are not authorized to perform operation (cls:createLogset) resource (qcs::cls:ap-guangzhou::logset/) has no permission"
- 腾讯云工程师
  您好，辛苦前往访问管理控制台：https://console.cloud.tencent.com/cam/role 
  先检查一下 SCF_QcsRole是否有QcloudAccessForScfRole这个策略，
  如果有,辛苦在检查一下日志集是否超限。在新建函数时候点击高级配置，选择自定义投递下拉已有日志集
  
2.调用请求时返回 {"errorCode":-1,"errorMessage":"Task memory exceeded 128 MB","statusCode":434}
- 因为jvm吃内存，所以请把云函数的内存调整到256MB再次尝试，如果不行继续拉大
  