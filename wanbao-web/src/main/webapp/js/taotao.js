var TT = TAOTAO = {
	checkLogin : function(){
		var _token = $.cookie("TT_TOKEN");
		if(!_token){
			return ;
		}
		$.ajax({
			url : "http://sso.taotao.com/service/user/query/" + _token,
			dataType : "jsonp",
			type : "GET",
			success : function(data){
				// 后端本来返回的是字符串数据,不过由于使用的jsonp传递json,因此data是已经浏览器解析过的数据
				// data是对象,而不是字符串 => 不能再使用JSON.parse !!!
				//var _data = JSON.parse(data);
					var html =data.username+"，欢迎来到淘淘！<a href=\"http://www.taotao.com/user/logout.html\" class=\"link-logout\">[退出]</a>";
					$("#loginbar").html(html);
			},
			error:function(data){
				//alert("执行失败！");
				//alert(data.status);
				//alert(data.username);
				//alert(data);
			}
		});
	}
}

$(function(){
	// 查看是否已经登录，如果已经登录查询登录信息
	TT.checkLogin();
});