本来预想的是mysql读写分离(主从复制),但是后面改造其他系统比较麻烦
--- 于2021.3.2取消了读写分离
涉及内容如下:
    .jdbc.properties:取消了主从数据库配置
    .com.wanbao.manage.datasource:这部分是读写分离的主要代码,暂时不用删除
    .applicationContext.xml:取消了主从数据库的配置
    .applicationContext-transaction.xml:事务策略,暂时不用取消修改也行
