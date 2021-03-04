
@[toc]
# 各工程概述
	.wanbao-parent：管理整个商城项目的依赖(pom打包，使用maven继承功能)
	.wanbao-common：各项目通用的代码(jar打包)
	.wanbao-manage：(pom打包，使用maven的聚合功能)
	  => 下含子模块(将manage工程拆分，方便共享)：
		.pojo    (jar)
		.mapper  (jar)
		.service (jar)
		.web     (即controller,war打包!!)  => 同时是后台系统   端口：8081
		子模块之间依赖关系:web -> service -> mapper -> pojo
		为什么要分为几个模块? => https://blog.csdn.net/weixin_45558363/article/details/106029557?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.nonecase
	.wanbao-web:前台系统				                    端口：8082
	.wanbao-sso:单点登录系统                                 端口：8083
	.wanbao-order:订单系统                                  端口：8084
	.wanbao-search 搜索系统                                 端口：8085
	.wanbao-cart:购物车系统								    端口：8086
	
	.wanbao-sso-query-api：查询用户信息改用RPC方式,此项目是公共接口(RPC的提供者、调用者都会使用)
	.wanbao-sso-query-service：提供sso、cart的RPC服务(前台系统调用此服务) 端口:8087
	
	.wanbao-static:存储所有公共的静态资源,通过nginx处理静态资源(部分非公共的静态资源仍然在原项目下)
	.wanbao-upload:上传的文件资源
	
=> 需要安装的工程:parent、common、manage、sso-query-api  

# 系统间交互
- 后台系统  
.新增商品后 => 通知前台/搜索系统(通过rabbitmq)  
.更新商品后 => 通知前台/搜索系统(通过rabbitmq)  


- 前台系统  
.验证用户是否登录 => 访问SSO提供的接口  
.提交用户订单 => 访问ORDER系统提供的接口  
.首页点击搜索后 => 向搜索系统发起请求,然后跳转到搜索系统的结果界面  
.收到后台系统更改(update、delete)商品的消息后(通过rabbitmq) => 删除前台缓存  


- SSO系统  
注:SSO下面只有login、register页面;更多的时候需要的是验证用户是否登录,这需要前台系统调用SSO提供的接口  
.在SSO系统完成登录后 => 会在SSO系统和前台系统的前端cookie中写入token,然后跳转到前台系统  

- ORDER系统  
.主要给前台系统提供接口  


- SEARCH系统  
注:SEARCH下面只有search页面  
.search系统的搜索功能通过solorj向solor发起请求,solor向search系统返回搜索结果,然后search系统再向浏览器返回搜索结果界面  
.收到后台系统更改(update、delete)商品的消息后(通过rabbitmq) => 将更新同步到solor中  

- CART系统  
注:CART下面只有cart页面  
.cart系统将指定id的商品加入购物车时 => 需要通过后台系统的接口查询商品详细信息  
.cart系统的登陆拦截器中 => 需要通过SSO系统的接口查询用户是否已登录(dubbo)  

# 数据库
	.tb_item_cat        => 存储商品的目录树(类目id、类目名、是否为父类...)
	.tb_item            => 存储每个商品的相应信息(商品id、价格、库存、图片、状态...)
	.tb_item_desc       => 存储每个商品的描述信息(商品id、商品描述;与tb_item配合使用,拆成两个表是为了减小查询压力)
	
	.tb_item_param      => 商品规格参数模板表(模板id、商品类目id、参数模板格式;一个商品类目对应一条采规格参数模板)
	.tb_item_param_item => 商品规格参数数据表(商品id、商品参数套上模板后的数据..;与tb_item_param配合使用)

	.tb_content_category=> 广告的类别表(类目id、类目名、是否为父类...;类似于tb_item_cat; 与tb_content配合使用)
	.tb_content         => 广告的内容表(内容id、标题、图片、描述...;存储首页每一条广告的具体信息)
	
	.tb_user            => 存储注册用户信息(自增id、用户名、密码、手机、邮箱...)	
	
	.tb_order           => 订单表(订单id、价格、用户id、订单状态、物流号、买家评价等)
	.tb_order_item      => 商品订单关系表(订单id、商品id、数量、商品单价、商品总额、商品标题等)
	.tb_order_shipping  => 主要是收货人信息(订单id、地址、姓名、电话等)
	
	.tb_cart            => 购物车信息(用户id、商品id、价格、数量...)
	

# API
接口调用示例(只写了一部分,更多接口参考各controller)  
功能并不完善,尤其是前端部分存在较多问题,补充完成...

## 后台系统(manage)
- 后台管理系统主页  
方法:GET  
URL:IP/rest/page/index  
参数:无  
返回值:页面  

- 新增商品  
方法:POST  
URL:IP/rest/item/  
参数:商品信息的表单数据=> Item类中包含的字段+desc+itemParams(详见ItemController)  
返回值:只有状态码,没有其他信息 

- 查询商品列表  
方法:GET  
URL:IP/rest/item?page=1&rows=30  
参数:page(查询第几页)、rows(每页多少条记录)  
返回值:查询到的所有记录构成的json数据  

- 获取商品的描述信息  
方法:GET  
URL:IP/rest/item/desc/1217594340  
参数:itemId(商品编号,即URL中最后的数字)  
返回值:json数据,其中包含描述该商品的html界面

- 获取商品类别名称  
方法:GET  
URL:IP/rest/item/cat/getCatName?cid=76  
参数:cid(商品类别id)  
返回值:字符串,是该类别商品对应的类别名称(如:平板电视)  

- (编辑后)更新商品信息  
方法:PUT(注意不是POST!!)  
URL:IP/rest/item  
参数:商品信息的表单数据=> Item类中包含的字段+desc+itemParams(详见ItemController)  
返回值:只有状态码,没有其他信息  

- 图片上传  
方法:  
URL:  
参数:  
返回值:  

--------------------------------------------------------------------------------------  

- 新增规格参数模板  
方法:POST  
URL:IP/rest/item/param/76  
参数:商品类别编号(url中最后的数字)+该类商品规格对应的json字符串(表单数据)  
返回值:只有状态码,没有其他信息  

- 查询产品规格参数模板列表  
方法:GET  
URL:IP/rest/item/param/list?page=1&rows=20  
参数:page(要查询第几页)、rows(每页多少调记录)  
返回值:json格式的参数模板  

- 查询商品的规格参数  
方法:GET  
URL:IP/rest/item/param/item/1474391928  
参数:itemId(url最后的数字,商品id)  
返回值:该商品的规格参数(json格式)  

--------------------------------------------------------------------------------------  

- 查询内容分类列表(即查询该内容下包含的子内容列表)  
方法:GET  
URL:IP/rest/content/category?id=30  
参数:id,内容的id(默认0);  
返回值:返回该id下的字内容信息,json数组格式  

- 新增内容分类  
方法:POST  
URL:IP/rest/content/category  
参数:ContentCategory  
返回值:ContentCategory,json格式  

- 重命名内容分类节点  
方法:PUT  
URL:IP/rest/content/category  
参数:id(要重命名的节点)+name(新名字) => 表单提交  
返回值:无  

- 删除内容分类节点  
方法:POST(按照REST应该是DELETE,但是DELETE无法在body中传递参数;在实现时表面使用POST,但是用_method字段指定为DELETE方法,后台的web.xml中使用过滤器提取参数)  
URL:IP/rest/content/category  
参数:ContentCategory => 表单提交  
返回值:无  

- 新增内容  
方法:POST  
URL:IP/rest/content  
参数:Content(具体字段见Content类) => 表单提交  
返回值:无  

- 查找某个内容分类下的所有具体内容  
方法:GET  
URL:IP/rest/content?categoryId=76&page=1&rows=30  
参数:categoryId(内容类别的id)+page(查询第几页)+rows(每页多少条记录)  
返回值:所有内容的列表,json格式  

--------------------------------------------------------------------------------------

以下为后台对其他系统提供的api......
- 获取所有商品类目的类目树  
方法:GET  
URL:IP/rest/api/item/cat  
参数:无  
返回值:json格式的目录树  

## 前台系统(web)
注:前台系统中的数据都是通过httpclient向后台查询所有,并非直接查数据库  
- 显示商品详情数据  
方法:GET  
URL:IP/item/1474391928.html 或者IP/service/item/1474391928.html  
参数:itemId(即URL最后的数字,html只是为了伪静态)  
返回值:整个商品详情页面  

- 删除某商品信息在前台redis中的缓存  
方法:POST  
URL:IP/item/cache/1474391928.html  
参数:itemId(即URL最后的数字)  
返回值:无  
注:后台的信息更新后,通过这个接口通知前台删除旧的redis缓存 => 系统间耦合较大,应该使用消息队列...  

- 跳转到订单确认页  
方法:GET  
URL:IP/order/1474391928.html或者IP/service/order/1474391928  
参数:itemId(路径最后一位)  
返回值:订单确认页面  


......

## 单点登录系统(sso)
注:sso有自己的redis、且可直接查询mysql中user表  
- 跳转到注册页面  
方法:GET  
URL:IP/user/register.html  
参数:无  
返回值:注册页面  

- 检查数据是否可用(即检查该数据对应的用户是否已经存在)  
方法:GET  
URL:IP/service/user/zhangsan/1  
参数:param(路径中的倒数第二个,前端输入的数据,将要检查它是否已经存在) + type(路径中最后一个=> 1:用户名,2:电话,3:邮箱)  
返回值:bool值(true表示该数据不存在,所以可用; false表示该数据存在,所以不可用)  

- 用户注册  
方法:POST  
URL:IP/service/user/doRegister  
参数:username+password+email(表单格式)  
返回值:业务状态码+可选的数据  

- 跳转到登录页面  
方法:GET  
URL:IP/user/login.html  
参数:无  
返回值:登录页面  

- 登录  
方法:POST  
URL:IP/service/user/doLogin  
参数:username+password(表单格式)  
返回值:业务状态码  

- 根据token查询已登录用户的信息(弃用,更好的方式是通过dubbo)  
方法:GET  
URL:IP/service/user/query/a9e7edcb228cce41729acee51e9975f1  
参数:token(路径中最后一个字段)  
返回值:该用户信息  

- 根据token查询已登录用户的信息(dubbo)  
方法:GET  
URL:IP/user/a9e7edcb228cce41729acee51e9975f1(这里ip应该是ssoquery的地址)  
参数:token(路径中最后一个字段)  
返回值:该用户信息  

## 订单系统(order)
- 创建订单  
方法；POST  
URL:IP/order/create  
参数:订单信息(json数据,不是表单!! => 参考Order类的字段)  
返回值:业务状态码+可选的消息  

- 根据订单号查询相关相关信息  
方法:GET  
URL:IP/order/query/31593943846034   
参数:orderId(路径的最后一个)  
返回值:订单相关json数据  

- 根据用户名分页查询订单信息  
方法:GET  
URL:IP/order/query/zhang123/1/10  
参数:buyerNick(路径倒数第三,买家昵称)+page(路径倒数第二,查询第几页)+count(路径倒数第一,每页几条记录)  
返回值:该用户对应的订单信息(Order的json数据)  

- 修改订单信息  
方法:POST  
URL:ip/order/changeOrderStatus  
参数:新的订单信息(json数据,参考Order类)  
返回值:业务状态码+一条消息  

## 搜索系统(search)
- 按照关键字搜索商品  
方法:GET  
URL:IP/search.html?q=tcl&page=1  
参数:q(关键字)+page(第几页)  
返回值:search结果页面  

## 购物车系统(cart)
注:cart可直接查询/修改mysql中cart表  
- 将商品加入到购物车  
方法:GET  
URL:IP/cart/1473155119.html 或者   IP/service/cart/1473155119
参数:itemId(路径最后一个)  
返回值:购物车列表界面(经过重定向) => 如果未登录:会更新前端的cookie信息; 如果已登录:直接更新cart数据库表  
注:这里只是将商品数量+1 => 待改进为指定数量  

- 查询购物车列表  
方法:GET  
URL:IP/cart/list.html  
参数:无  
返回值:购物车列表界面 => 若未登录:界面只会显示cookie中的购物车信息; 若已登录:查数据库cart并显示用户购物车中所有信息  

- 更新购物车中商品数量  
方法:IP/cart/update/num/1473155119/2  
参数:itemId(路径倒数第二个)+num(路径倒数第一个)  
返回值:无 => 如果未登录:会更新前端cookie信息; 如果已登录:直接更新cart数据库表  

- 删除购物车中的商品  
方法:GET  
IP/cart/delete/1473155119.html 或者 IP/service/cart/delete/1473155119  
参数:itemId(路径中最后一个)  
返回值:重定向到购物车界面 => 如果未登录:会更新前端cookie信息; 如果已登录:直接更新cart数据库表

# 技术栈
SSM、mysql(读写分离)、nginx(反向代理)、redis(缓存)、solor(搜索)、rabbitmq(通知删除缓存)、dubbo(RPC优化单点登录)、zookeeper(注册中心)...

# note
经检验,除了前端界面的某些链接地址不正确外,以上罗列的接口基本无误;  
由于系统较多,启动/运行时遇到错误很有可能是依赖的相关系统没有启动或者没有安装配置正确(比如nginx、solor、dubbo、rabbitmq、zk等),务必耐心寻找问题  

	


	
	
	
	
		