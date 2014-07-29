$(function () {
	var todos = $('#todocontainer');
	
	init();
	
	function init() {
		$.ajax({type: 'GET',
		 		url: 'apiv1/todos'
		}).done(populateTodoList);
	}
	
	function populateTodoList(todo_list) {
		var newTodoItem = createAddNewTodoWidget();
		todos.append(newTodoItem);
		
		$.each(todo_list, function(i,todo_data) {
			var todo = createTodoItemWidget(todo_data);
			todos.append(todo);
		});
	}
	
	function updateTodo(data) {
		$.ajax({type: 'PUT',
				url: 'apiv1/todos/' + data.id,
				contentType: 'application/json',
				data: JSON.stringify(data),
				statusCode: {
					204: function() {
						console.log('Updated Todo: ' + data.title);
					}
				}
		});
	}
	
	function newTodo(data) {
		$.ajax({type: 'POST',
				url: 'apiv1/todos',
				contentType: 'application/json',
				data: JSON.stringify(data),
				statusCode: {
					201: function(result) {
						data.id = result.id;
						console.log('Created new Todo: ' + data.title);
					}
				}
		});
	}
	
	function deleteTodo(data) {
		$.ajax({type: 'DELETE',
				url: 'apiv1/todos/' + data.id,
				statusCode: {
					204: function(result) {
						console.log('Deleted Todo: ' + data.title);
					}
				}
		});
	}
	
	function createAddNewTodoWidget() {
		var widget = $('<div></div>');
		var title = $('<input type="text" class="todoaddtitle" placeholder="I want to.." tabIndex="1">');
		var body = $('<textarea class="todoaddbody" maxlength="1000" rows="4" placeholder="Describe your task in detail:\n1.\n2.\n3." tabIndex="2" wrap="hard">');
		var save = $('<span class="todoadd fa fa-plus-circle fa-4"></span>');
		
		save.click(function() {
			var data = {
				title: title.val(),
				body: body.val(),
				done: false
			};
			
			if(data.title.trim().length > 0) {
				newTodo(data);
				todos.append(createTodoItemWidget(data));
				title.val('');
				body.val('');
			}
		});
		
		widget.append(title);
		widget.append(save);
		widget.append(body);
		
		return widget;
	} 
	
	function createTodoItemWidget(data) {
		var todo = $('<li class="todoitem"></td>');
		var span = $('<div></div>');
		
		var check = $('<span class="fa fa-4 todostatus"></span>');
		var discard = $('<span class="fa fa-trash-o fa-2 tododelete"></span>');
		var title = $('<span class="todotitle"></span>');
		var body = $('<span class="todobody"></span>');
		
		function checkTodo(value) {
			if (value) {
				check.removeClass('fa-circle-thin');
				check.addClass('fa-check-circle');
				title.addClass('struck');
				body.addClass('struck');
			} else {
				check.addClass('fa-circle-thin');
				check.removeClass('fa-check-circle');
				title.removeClass('struck');
				body.removeClass('struck');
			}
		}	
		
		checkTodo(data.done);
		check.click(function() {
			data.done = !data.done;
			checkTodo(data.done);
			updateTodo(data);
		});
		
		discard.click(function() {
			todo.fadeTo("fast",0.00,function() {
				todo.slideUp("fast",function() {
					todo.remove();
				});
			});
			deleteTodo(data);
		});
		title.text(data.title);
		body.text(data.body);
		
		span.append(check);
		span.append(discard);
		span.append(title);
		span.append(body);
		
		todo.append(span);
		
		return todo;
	}
});
