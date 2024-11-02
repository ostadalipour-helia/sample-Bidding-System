<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="server.ws.DatabaseUtil"%>
<%@ page import="server.ws.Item"%>
<%@ page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <title>WELCOME TO LAB TEST 2</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f0f0f0;
            margin: 20px;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: white;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .item-option {
            margin: 15px 0;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .item-description {
            font-size: 0.9em;
            color: #666;
            margin-left: 25px;
        }
        .submit-btn {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>WELCOME TO LAB TEST 2</h1>
        <form action="LoginServlet" method="GET">
            <p>Please select the service you want to invoke:</p>
            
            <% 
            List<Item> items = DatabaseUtil.getAllItems();
            for(Item item : items) {
            %>
                <div class="item-option">
                    <input type="radio" id="<%= item.getCode().toLowerCase() %>" 
                           name="checked" value="<%= item.getCode() %>">
                    <label for="<%= item.getCode().toLowerCase() %>">
                        <%= item.getName() %>
                    </label>
                    <div class="item-description">
                        <%= item.getDescription() %>
                    </div>
                </div>
            <% } %>
            
            <input type="submit" value="Select Item" class="submit-btn">
        </form>
    </div>
</body>
</html>