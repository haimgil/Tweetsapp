Parse.Cloud.beforeSave(Parse.User, function(request, response) {
    var query = new Parse.Query(Parse.User);
    query.equalTo("email", request.object.get("email"));
	query.equalTo("emailVerified",true);
    
	query.first({
		success: function(object) {
			if (object){
				response.error("email_in_use");
			}else{
				response.success();
			}
		},
		error: function(error) {
			response.error(error);
		}
    });
});