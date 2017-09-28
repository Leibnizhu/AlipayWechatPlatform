# Alipay-Wechat-Platform
## 命名
原来命名是Wechat-Alipay-Platform，但这样缩写WAP、可能引起歧义，所以改成Alipay-Wechat-Platform，缩写AWP（还是有歧义，但至少不是一个领域的）。

## API
### 微信授权
申请微信授权。web服务需要授权时，向用户发送重定向到该接口。
请求地址：~~http://localhost:8083~~/awp/wxOauth/apply/{body}
参数：body，格式为变种Base64编码的JSON，请用~~http://localhost:8083~~/awp/base64.html进行编码。  
```json
{
    "eid":web项目使用的公众号在本项目中的用户ID
    "type":0=静默授权，只能获取OpenID，1=正常授权，会弹出授权确认页面，可以获取到用户信息
    "callback":授权成功后调用的web项目回调接口地址,请使用完整地址,
                 回调时会使用GET方法，加上rs参数，
                 如果静默授权，rs参数内容就是openid
                 如果正常授权，rs参数内容是turingBase64加密的授权结果(JSON)
}
```

### 微信支付
待续……

### 支付宝支付
待续……