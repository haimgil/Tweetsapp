Parse.Cloud.beforeSave(Parse.User, function(request, response) {
    var query = new Parse.Query(Parse.User);
    query.equalTo("username", request.object.get("username"));
    
	query.first({
		success: function(object) {
			if (object){
				response.error("username_in_use");
			}else{
				response.success();
			}
		},
		error: function(error) {
			response.error(error);
		}
    });
});