appService.service('UserService', ['$resource', '$q', function ($resource, $q) {
    var user_resource = $resource('', {}, {
        load_user: {
            method: 'GET',
            url: '/user'
        },
        find_users: {
            method: 'GET',
            isArray: true,
            url: '/users'
        },
        create_or_update_user: {
            method: 'POST',
            url: '/users/:tableViewOperType'
        },
        totalPage: {
            method: 'GET',
            url: '/user/totalPage'
        },
        delete_user:{
            method: 'DELETE',
            url: '/user/delete/:userId'
        },
        enabled_users: {
            method: 'DELETE',
            url: '/user/enabled/:userId/:state'
        }
    });
    return {
        load_user: function () {
            var finished = false;
            var d = $q.defer();
            user_resource.load_user({},
                function (result) {
                    finished = true;
                    d.resolve(result);
                },
                function (result) {
                    finished = true;
                    d.reject(result);
                });
            return d.promise;
        },
        find_users: function (keyword, offset) {
            var d = $q.defer();
            user_resource.find_users({
                    keyword: keyword,
                    offest: offset
                },
                function (result) {
                    d.resolve(result);
                },
                function (result) {
                    d.reject(result);
                });
            return d.promise;
        },
        createOrUpdateUser: function (user) {
            var d = $q.defer();
            user_resource.create_or_update_user({tableViewOperType: user.tableViewOperType}, user,
                function (result) {
                    d.resolve(result);
                },
                function (result) {
                    d.reject(result);
                });
            return d.promise;

        },
        totalPage: function () {
            var d = $q.defer();
            user_resource.totalPage({},
                function (result) {
                    d.resolve(result);
                },
                function (result) {
                    d.reject(result);
                });
            return d.promise;
        },
        delete_user:function (userId) {
            var d = $q.defer();
            user_resource.delete_user({ userId: userId},
                function (result) {
                    d.resolve(result);
                },
                function (result) {
                    d.reject(result);
                });
            return d.promise;
        },
        enabled_user:function (userId,state) {
            var d = $q.defer();
            user_resource.enabled_users({ userId: userId,state:state==true?1:0},
                function (result) {
                    d.resolve(result);
                },
                function (result) {
                    d.reject(result);
                });
            return d.promise;
        }
    }
}]);
