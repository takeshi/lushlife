$ = jQuery
id = $("#page_id").attr("value")
socket = new WebSocket("ws://#{window.location.host}/socket?id=#{id}")

socket.onmessage = (data) ->
	console.log(data)


@testMessage = ()->
	socket.send("hello")


@putMessage =(id,message)->
	$.ajax(
		type:"PUT"
		url:"/socket"
		data:
		   id:id
		   message:message
	)