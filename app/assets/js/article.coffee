$ = jQuery

validationError = (error)->
  $("#article_"+error.property)
    .attr("data-original-title",error.message)
    .tooltip("show")
  $("#fieldset_article_"+error.property)
    .attr("class","control-group error")

$('.article_input').tooltip({placement:"left"})

@persistArticle = persistArticle = (open)->
  id = $("#article_id").attr "value"
  $.ajax
   type :"PUT"
   url : "/rerender/article.json"
   contentType:"application/json"
   dataType:"json"
   success :(data)->
      $("#myModal").modal("hide")
      $("#main_content").html data.content
      prettyPrint()
   error: (data) ->
      $('#myModal').modal("hide")
      $('.article_input').attr("data-original-title","")
      $('fieldset').attr("class","control-group")
      errors = JSON.parse(data.responseText).errors
      if(errors)
        validationError error for error in errors
   data:
   	 JSON.stringify      
      _id:
        $oid:$("#article__id").attr "value"
   	 	id:$("#article_id").attr "value"
      open:open
   	 	title:$("#article_title").attr "value"
   	 	content:$("#article_content").attr "value"
      owner:$("#article_owner").attr "value"

$("#persist_btn").live "click",(e) -> 
	persistArticle(true)
	$("#myModal").modal("show")

$("#prepersist_btn").live "click",(e) -> 
  persistArticle(false)
  $("#myModal").modal("show")


$("#edit_btn").live "click",(e) ->
   id = $("#article_id").attr "value"
   $.ajax
    type:"GET"
    url:"/rerender/article/#{id}.json"
    success:(data)->
      $("#main_content").html data.content
    error: (data) ->
     console.log(data)

$('#myModal').modal(
  backdrop:true
  keyboard:false
  show:false
)

$('#preview_btn').live "click",(e) ->
 $.ajax
   type :"POST"
   url : "/rerender/preview"
   contentType:"application/json"
   dataType:"json"
   success :(data)->
     $("#preview").html data.content
     prettyPrint()
   error: (data) ->
     console.log(data)
   data:
     JSON.stringify      
      _id:
        $oid:$("#article__id").attr "value"
      id:$("#article_id").attr "value"
      title:$("#article_title").attr "value"
      content:$("#article_content").attr "value"
      open:true

###
  Delete Button
###

$('#delete_btn').live "click",()->
  $("#deleteModal").modal("show")

$('#delete_ok_btn').live "click",()->
  $.ajax 
   type :"DELETE"
   dataType:"json"
   success :(data)->
    window.location = "/"
   error: (data) ->
     console.log(data)
     $("#deleteModal").modal("hide")

$('#delete_cancel_btn').live "click",()->
  $("#deleteModal").modal("hide")

$('#deleteModal').modal(
  backdrop:true
  keyboard:false
  show:false
)


###
  New Button
###

$('#new_article_btn').live "click",()->
  $("#newArticleModal").modal("show")

$('#new_article_ok_btn').live "click",()->
  window.location =$("#twitter_name").attr("value")+"/"+ $("#new_article_id_input").attr("value")

$('#new_article_cancel_btn').live "click",()->
  $("#newArticleModal").modal("hide")


$('#newArticleModal').modal(
  backdrop:true
  keyboard:false
  show:false
)
