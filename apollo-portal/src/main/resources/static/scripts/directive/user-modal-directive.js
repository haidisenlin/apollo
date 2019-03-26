directive_module.directive('usermodal', userModalDirective);

function userModalDirective(toastr, AppUtil,EventManager, UserService) {
    return {
        restrict: 'E',
        templateUrl: '../../views/component/user-modal.html',
        transclude: true,
        replace: true,
        scope: {
            user: '=',
            item: '='
        },
        link: function (scope) {
            scope.doItem = doItem;

            function doItem() {
                if(scope.item.tableViewOperType == 'create'){
                    scope.user.tableViewOperType='create'
                }else{
                    scope.user.tableViewOperType='update'
                }
                UserService.createOrUpdateUser(scope.user).then(function (result) {
                    if (scope.item.tableViewOperType == 'create')
                        toastr.success("创建用户成功");
                    else
                        toastr.success("修改用户成功");
                    AppUtil.hideModal('#userModel');
                    EventManager.emit(EventManager.EventType.REFRESH_USER_LIST,
                        {
                            namespace: scope.toOperationNamespace
                        });
                }, function (result) {
                    if (scope.item.tableViewOperType == 'create')
                        AppUtil.showErrorMsg(result, "创建用户失败");
                    else
                        AppUtil.showErrorMsg(result, "修改用户失败");
                })
            }
        }
    }
}


