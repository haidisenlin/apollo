directive_module.directive('myInput',function(){
    return{
        restrict : "AE",
        scope : {
            value  :"@",
            fun : "&"
        },
        template :'<div class="switch" data-on-label="启用" data-off-label="禁用"></div>',
        replace : true,
        link : function(scope,element,attr){
            var $input = $('<input type="checkbox" name="switch" checked >');
            $(element[0]).append($input);
            $(element[0]).children().bootstrapSwitch({
                'state': JSON.parse(scope.value).enabled=="1"?true:false,
                'size':'small',
                onSwitchChange : function(target,state){
                    $(this).bootstrapSwitch('state',!state,true);
                    var _self =$(this)
                    //state是开关的状态
                    scope.fun({toEditItem:JSON.parse(scope.value),state:state}).then(function(result){
                        //如果返回成功则什么都不做
                        _self.bootstrapSwitch('toggleState',true);
                    },function(result){
                        //如果返回失败，则要回滚状态
                    })
                }
            })
        }
    }
});