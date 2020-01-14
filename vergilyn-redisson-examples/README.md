# redisson-example

1. 
```
2019-10-09 15:24:51.077  WARN 9928 --- [main] config: 'reconnectionTimeout' setting in unavailable. Please use 'failedSlaveReconnectionInterval' setting instead!
2019-10-09 15:24:51.083  WARN 9928 --- [main] config: 'failedAttempts' setting in unavailable. Please use 'failedSlaveCheckInterval' setting instead!
```

但是`org.redisson.config.SingleServerConfig`或`org.redisson.config.BaseConfig`中并不存在属性`failedSlaveReconnectionInterval / failedSlaveCheckInterval`。  
其存在于`org.redisson.config.BaseMasterSlaveServersConfig`。

但xml形式的`single-server`却支持`failedSlaveReconnectionInterval / failedSlaveCheckInterval`。
如果json形式更改为`reconnectionTimeout -> failedSlaveReconnectionInterval, failedAttempts -> failedSlaveCheckInterval`会异常！
