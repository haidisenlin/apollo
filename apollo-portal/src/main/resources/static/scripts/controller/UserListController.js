user_list_module.controller('UserListController',
    ['$scope', '$window', 'toastr', 'AppUtil', 'UserService','EventManager','$q',
        UserListController,]);

function UserListController($scope, $window, toastr, AppUtil, UserService,EventManager,$q) {
    $scope.createItem = createItem;
    $scope.editItem = editItem;
    $scope.preDeleteItem = preDeleteItem;
    $scope.deleteItem = deleteItem;
    $scope.user={};

    UserService.totalPage().then(function (result) {
        $scope.pageSize = 10;
        $scope.pages = result.totalPage;
        $scope.newPages = $scope.pages > 5 ? 5 : $scope.pages;
        $scope.pageList = [];
        $scope.selPage = 1;

        //分页要repeat的数组
        for (var i = 0; i < $scope.newPages; i++) {
            $scope.pageList.push(i + 1);
        }
        //打印当前选中页索引
        $scope.selectPage = function (page) {
            //不能小于1大于最大
            if (page < 1 || page > $scope.pages) return;
            //最多显示分页数5
            if (page > 2) {
                //因为只显示5个页数，大于2页开始分页转换
                var newpageList = [];
                for (var i = (page - 3); i < ((page + 2) > $scope.pages ? $scope.pages : (page + 2)); i++) {
                    newpageList.push(i + 1);
                }
                $scope.pageList = newpageList;
            }
            $scope.selPage = page;
            getUserList(page);
            $scope.isActivePage(page);
            console.log("选择的页：" + page);
        };
        //设置当前选中页样式
        $scope.isActivePage = function (page) {
            return $scope.selPage == page;
        };
        //上一页
        $scope.Previous = function () {
            $scope.selectPage($scope.selPage - 1);
        };
        //下一页
        $scope.Next = function () {
            $scope.selectPage($scope.selPage + 1);
        };

        $scope.selectPage($scope.selPage);

    });


    //获取用户列表
    function getUserList(page) {
        $scope.users = [];
        var offest = (page - 1) * 10;
        UserService.find_users("", offest)
            .then(function (result) {
                result.forEach(function (item) {
                    $scope.users.push(item);
                });
            })
    }

    //新增用户信息
    function createItem() {
        $scope.item = {
            tableViewOperType: 'create'
        };
        $scope.user ={};
        AppUtil.showModal('#userModel');
    }
    //修改用户信息
    function editItem(toEditItem) {
        $scope.item = {
            tableViewOperType: 'update'
        };
        $scope.user ={
            username:toEditItem.userId,
            password:"",
            email:toEditItem.email
        };
        $("#userModel").modal("show");
    }



    //删除用户
    var toDeleteItemId = 0;
    function preDeleteItem(itemId) {
        toDeleteItemId = itemId;
        $("#deleteUserDialog").modal("show");
    }
    function deleteItem() {
        UserService.delete_user(toDeleteItemId).then(
            function (result) {
                toastr.success("删除成功!");
            }, function (result) {
                toastr.error(AppUtil.errorMsg(result), "删除失败");
            });
    }
    //禁用
    $scope.enabled = function(toEditItem,state) {
        var d = $q.defer();
        UserService.enabled_user(toEditItem.userId,state).then(
            function (result){
                if(state){
                    d.resolve(result);
                    toastr.success("启用成功!");
                }else{
                    d.resolve(result);
                    toastr.success("禁用成功!");
                }
            }, function (result) {
                d.reject(result);
                toastr.error(AppUtil.errorMsg(result), "删除失败");
            });
        return d.promise
    }
    EventManager.subscribe(EventManager.EventType.REFRESH_USER_LIST,
        function () {
            getUserList(1);
        });
}