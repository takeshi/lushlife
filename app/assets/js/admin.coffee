$ = jQuery


$(".admin_btn").live "click",(e)->
	console.log e
	console.log 
	console.log $(e.target).attr("admin")

	$.ajax
		url:"/rerender/admin"
		type:"POST"
		dataType:"JSON"
		data:
			oid:$(e.target).attr("oid")
			admin:$(e.target).attr("admin")
		success:(d)->
			$(e.target).attr("oid",d.oid)
			$(e.target).attr("admin",d.admin)
			$(e.target).html d.admin
		error:(e)->
			console.log(e)
	