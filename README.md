# Alipay-Wechat-Platform
## 命名
原来命名是Wechat-Alipay-Platform，但这样缩写WAP、可能引起歧义，所以改成Alipay-Wechat-Platform，缩写AWP（还是有歧义，但至少不是一个领域的）。
## 意义
本项目旨在减少Web项目关于微信、支付宝的重复代码，以及解决公众号的安全域名只能配置一个的问题。  
未来还可以托管Access Token。
本项目部署后，将公众号安全域名配置到本项目的域名，此后多个web项目（不同域名）可以使用同一个公众号 ~~（理论上）~~ 。  
## 技术栈
原来：`Vue.js + Spring Boot + Druid + MySQL`  
现在：`Vue.js + Vert.X(Core+Web+JDBC) + HikariCP + MySQL(可能换成PostgreSQL)`

## Maven子模块
- `awp-base`: PoJo类，工具类（通用工具、微信工具、支付宝工具）
- `awp-final`: 最终打成Vert.X整合包，包括后台管理和服务的入口
- `awp-verticle-admin`: 后台管理页面，需要JWT授权登录
- `awp-verticle-base`: Verticle基础通用类，目前只有一个`SubRouter`接口
- `awp-verticle-db`: 数据库相关类
- `awp-verticle-message`: 微信、支付宝(模板/客服/图文)消息发送服务(TODO)
- `awp-verticle-oauth`: 微信、支付宝(TODO)授权服务
- `awp-verticle-pay`: 微信、支付宝支付服务(TODO)

## 启动方式
### 命令行启动
```bash
mvn clean && maven package
java -jar awp-final/target/awp-0.0.1-SNAPSHOT-fat.jar run com.turingdi.awp.MainVerticle
```
### 调试
从`awp-final`子模块中的`com.turingdi.awp.MainLauncher`类启动项目即可。

## 后台管理页面
入口地址：http://localhost:8083/static/

## API
### 微信授权
申请微信授权。web服务需要授权时，向用户发送重定向到该接口。
请求地址：http://localhost:8083/wxOauth/apply/{body}
参数：body，格式为变种Base64编码的JSON，请用http://localhost:8083/static/page/base64.html进行编码。  
例如（请修改域名后，在微信打开，静默授权，授权后跳到百度首页(为了展示可以回调到任何地址)，观察地址，rs参数是你的OpenID）: http://localhost:8083/wxOauth/apply/bgNVIODVIfwpZOI2dADsO3DVIOD3TmLgZSI2KOgxIODVIOkBHCjsHfqB1YI2IfhMTmD2oY60T0cuHfqpZm8uHt6nIVp6OV~~
```json
{
    "eid":"web项目使用的公众号在本项目中的用户ID",
    "type":"0=静默授权，只能获取OpenID，1=正常授权，会弹出授权确认页面，可以获取到用户信息",
    "callback":"授权成功后调用的web项目回调接口地址,请使用完整地址,回调时会使用GET方法，加上rs参数，rs参数值是turingBase64加密的授权结果(JSON)"
}
```

### 微信支付
待续……

### 支付宝支付
待续……