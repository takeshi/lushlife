$ = jQuery

validationError = (error)->
  $("#article_"+error.property)
    .attr("data-original-title",error.message)
    .tooltip("show")
  $("#fieldset_article_"+error.property)
    .attr("class","control-group error")

$('.article_input').tooltip({placement:"left"})

@persistArticle = persistArticle = ()->
  id = $("#article_id").attr "value"
  $.ajax
   type :"PUT"
   url : "/api/article.json"
   contentType:"application/json"
   dataType:"json"
   success :(data)->
      $("#myModal").modal("hide")
      $("#main_content").html data.content
   error: (data) ->
      $('#myModal').modal("hide")
      $('.article_input').attr("data-original-title","")
      $('fieldset').attr("class","control-group")
      errors = JSON.parse(data.responseText).errors
      validationError error for error in errors

   data:
   	 JSON.stringify
   	 	id:$("#article_id").attr "value"
   	 	title:$("#article_title").attr "value"
   	 	content:$("#article_content").attr "value"

$("#persist_btn").live "click",(e) -> 
	persistArticle()
	$("#myModal").modal("show")

$("#delete_btn").live "click",(e) ->
   id = $("#article_id").attr "value"
   $.ajax
    type:"DELETE"
    url:"/api/article/#{id}.json"
    success:(data)->
      $("#main_content").html data.content
    error: (data) ->
     console.log(data)
$('#myModal').modal(
	backdrop:true
	keyboard:false
	show:false
)
