var todo_store = {};

$(function () {
	var widgets_container = $('#widgetscontainer');
	var todos_container = $('#todocontainer');
	var search_container = $('#searchresults');
	
	init();
	
	function init() {
		configureFunctionIcons({
			create: $('#create'),
			search: $('#search')
		});
		
		$.ajax({type: 'GET',
		 		url: 'apiv1/todos'
		}).done(populateTodoList);
	}
	
	function configureFunctionIcons(icons) {
		var newTodoItem = createAddNewTodoWidget();
		var searchTodoItem = createSearchWidget();
		
		widgets_container.append(newTodoItem);
		widgets_container.append(searchTodoItem);
		
		icons.create.click(function() {
			newTodoItem.show();
			searchTodoItem.hide();
		});
		
		icons.search.click(function() {
			searchTodoItem.show();
			newTodoItem.hide();
		});
		
		newTodoItem.hide();
		searchTodoItem.hide();
	}
	
	function populateTodoList(todo_list) {
		$.each(todo_list, function(i,todo_data) {
			var todo = createTodoItemWidget(todo_data);
			todos_container.append(todo);
			todo_store[todo_data.id] = todo;
		});
	}
	
	function newTodo(data,callback) {
		$.ajax({type: 'POST',
				url: 'apiv1/todos',
				contentType: 'application/json',
				data: JSON.stringify(data),
				statusCode: {
					201: function(result) {
						data.id = result.id;
						console.log('Created new Todo: ' + data.title);
						callback();
					}
				}
		});
	}
	
	function updateTodo(data,callback) {
		$.ajax({type: 'PUT',
				url: 'apiv1/todos/' + data.id,
				contentType: 'application/json',
				data: JSON.stringify(data),
				statusCode: {
					204: function() {
						console.log('Updated Todo: ' + data.title);
						callback();
					}
				}
		});
	}
	
	function deleteTodo(data,callback) {
		$.ajax({type: 'DELETE',
				url: 'apiv1/todos/' + data.id,
				statusCode: {
					204: function(result) {
						console.log('Deleted Todo: ' + data.title);
						callback();
					}
				}
		});
	}
	
	function searchTodos(input) {
		$.ajax({type: 'GET',
				url: 'apiv1/todos/search?q=' + encodeURIComponent(input.trim()),
				success: function(result) {
					console.log('Search hits: ' + result.length);
					
					search_container.empty();
					
					$.each(findMatchedTodos(result), function(_, m) {
						search_container.append(m);
					});
					
					search_container.show();
					todos_container.hide();
				}
		});
	}
	
	function findMatchedTodos(matches /* array of ids */) {
		return $.map(matches, function(id,_) {
			return todo_store[id].clone();
		});
	}
	
	function createAddNewTodoWidget() {
		var widget = $('<div id="addTodoWidget"></div>');
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
				newTodo(data, function() {
					var todo = createTodoItemWidget(data);
					
					todos_container.append(todo);
					todo_store[data.id] = todo;
					
					title.val('');
					body.val('');
				});
			}
		});
		
		widget.append(title);
		widget.append(save);
		widget.append(body);
		
		return widget;
	}
	
	function createSearchWidget() {
		var widget = $('<div id="searchwidget"></div>');
		var searchbox = $('<input type="text" id="searchbox" class="searchbox" placeholder="Search.." tabIndex="1">');
		var close = $('<span class="searchclose fa fa-times-circle fa-4"></span>')
		
		widget.keydown(function(ev) {
			if(event.which == 13) {
				searchTodos(searchbox.val());
			}
		});
		
		close.click(function() {
			searchbox.val('');
			search_container.hide();
			todos_container.show();
		});
		
		widget.append(searchbox);
		widget.append(close);
		
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
			var clone = $.extend({}, data);
			clone.done = !clone.done;
			
			updateTodo(clone, function() {
				data.done = clone.done;
				checkTodo(data.done);
			});
		});
		
		discard.click(function() {
			deleteTodo(data, function() {
				todo.fadeTo("fast",0.00,function() {
					todo.slideUp("fast",function() {
						todo.remove();
						delete todo_store[data.id];
					});
				});
			});
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
