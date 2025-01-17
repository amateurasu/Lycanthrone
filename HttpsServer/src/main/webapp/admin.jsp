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
    <link rel="apple-touch-icon" sizes="76x76" href="img/apple-icon.png">
    <link rel="icon" type="image/png" href="./img/favicon.ico">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <title>Submissions</title>
    <meta content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no'
          name='viewport'/>
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700,200" rel="stylesheet"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css"/>
    <link href="css/bootstrap.min.css" rel="stylesheet"/>
    <link href="css/light-bootstrap-dashboard.css?v=2.0.1" rel="stylesheet"/>
    <link rel="stylesheet" href="css/demo.css"/>
</head>

<body>
<div class="wrapper">
    <%@ include file="template/sidebar.jsp" %>
    <div class="main-panel">
        <%@ include file="template/navbar.jsp" %>

        <div class="content">
            <div class="container-fluid">
                <h3>Publish Year</h3>
                <table class="table table-hover table-striped table-bordered">
                    <thead>
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">Name</th>
                        <th scope="col">Submit date</th>
                        <th scope="col" colspan="2">Update</th>
<%--                        <th scope="col"></th>--%>
                    </tr>
                    </thead>
                    <tbody id="submissions">
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>

<script src="js/core/jquery.3.2.1.min.js" type="text/javascript"></script>
<script src="js/core/popper.min.js" type="text/javascript"></script>
<script src="js/core/bootstrap.min.js" type="text/javascript"></script>
<script src="js/plugins/bootstrap-switch.js"></script>
<script src="js/plugins/chartist.min.js"></script>
<script src="js/plugins/bootstrap-notify.js"></script>
<script src="js/plugins/jquery-jvectormap.js" type="text/javascript"></script>
<script src="js/plugins/moment.min.js"></script>
<script src="js/plugins/bootstrap-datetimepicker.js"></script>
<script src="js/plugins/sweetalert2.min.js" type="text/javascript"></script>
<script src="js/plugins/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="js/plugins/nouislider.js" type="text/javascript"></script>
<script src="js/plugins/bootstrap-selectpicker.js" type="text/javascript"></script>
<script src="js/plugins/jquery.validate.min.js" type="text/javascript"></script>
<script src="js/plugins/jquery.bootstrap-wizard.js"></script>
<script src="js/plugins/bootstrap-table.js"></script>
<script src="js/plugins/jquery.dataTables.min.js"></script>
<script src="js/plugins/fullcalendar.min.js"></script>
<script src="js/light-bootstrap-dashboard.js?v=2.0.1" type="text/javascript"></script>
<script src="js/statistic.js"></script>
</html>
