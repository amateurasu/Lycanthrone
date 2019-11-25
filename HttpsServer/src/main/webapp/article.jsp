<%@ page import="edu.fpt.comp1640.model.user.Coordinator" %>
<%@ page import="edu.fpt.comp1640.model.user.User" %>

<%
    Object _user = session.getAttribute("user");
    if (_user == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    User user = (User) _user;
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8"/>
    <link rel="apple-touch-icon" sizes="76x76" href="./img/apple-icon.png">
    <link rel="icon" type="image/png" href="./img/favicon.ico">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <title>Submissions</title>
    <meta content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no'
          name='viewport'/>
    <!--     Fonts and icons     -->
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700,200" rel="stylesheet"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css"/>
    <!-- CSS Files -->
    <link href="css/bootstrap.min.css" rel="stylesheet"/>
    <link href="css/light-bootstrap-dashboard.css?v=2.0.1" rel="stylesheet"/>
    <!-- CSS Just for demo purpose, don't include it in your project -->
    <link rel="stylesheet" href="css/demo.css"/>
</head>

<body>
<div class="wrapper">
    <%@ include file="template/sidebar.jsp" %>
    <div class="main-panel">
        <%@ include file="template/navbar.jsp" %>
        <div class="content">
            <div class="container-fluid">
                <h4 id="submit-name"></h4>
                <h5 id="submit-time"></h5>

                <button class="btn btn-primary btn-download"><i class="fa fa-download"></i> Download</button>
                <% if (user instanceof Coordinator) { %>
                <button id="selection" class="btn">
                    <i id="selection-icon" class="fa"></i> <span id="select-txt"></span>
                </button>
                <% } %>
                <table role="presentation" class="table table-striped">
                    <tbody id="preview" class="files"></tbody>
                </table>

                <br>
                <hr>
                <h5>Coordinators' comments</h5>

                <% if (user instanceof Coordinator) { %>
                <form><div class="input-group">
                    <input id="txt-comment" type="text" class="form-control">
                    <div class="input-group-append">
                        <button id="btn-send" type="submit" class="btn btn-outline-secondary">
                            <i class="fa fa-pencil"></i> Comment
                        </button>
                    </div>
                </div></form>
                <% } %>
                <hr>
                <br>
                <table role="presentation" class="table table-striped">
                    <tbody class="files" id="comments">
                    <tr>
                        <td><b>A Coordinator</b></td>
                        <td>Some comment</td>
                        <td>2019-04-30 00:00:00</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>

<script type="text/javascript" src="./js/core/jquery.3.2.1.min.js"></script>
<script type="text/javascript" src="./js/core/popper.min.js"></script>
<script type="text/javascript" src="./js/core/bootstrap.min.js"></script>
<script type="text/javascript" src="./js/plugins/bootstrap-switch.js"></script>
<script type="text/javascript" src="./js/plugins/chartist.min.js"></script>
<script type="text/javascript" src="./js/plugins/bootstrap-notify.js"></script>
<script type="text/javascript" src="./js/plugins/jquery-jvectormap.js"></script>
<script type="text/javascript" src="./js/plugins/moment.min.js"></script>
<script type="text/javascript" src="./js/plugins/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="./js/plugins/sweetalert2.min.js"></script>
<script type="text/javascript" src="./js/plugins/bootstrap-tagsinput.js"></script>
<script type="text/javascript" src="./js/plugins/nouislider.js"></script>
<script type="text/javascript" src="./js/plugins/bootstrap-selectpicker.js"></script>
<script type="text/javascript" src="./js/plugins/jquery.validate.min.js"></script>
<script type="text/javascript" src="./js/plugins/jquery.bootstrap-wizard.js"></script>
<script type="text/javascript" src="./js/plugins/bootstrap-table.js"></script>
<script type="text/javascript" src="./js/plugins/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="./js/plugins/fullcalendar.min.js"></script>
<script type="text/javascript" src="./js/light-bootstrap-dashboard.js?v=2.0.1"></script>
<script type="text/javascript" src="./js/article.js"></script>
</html>
