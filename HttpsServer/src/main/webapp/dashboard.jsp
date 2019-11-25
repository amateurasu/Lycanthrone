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
    <title>Dashboard</title>
    <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, shrink-to-fit=no'/>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Montserrat:400,700,200"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css"/>
    <link rel="stylesheet" href="css/bootstrap.min.css"/>
    <link rel="stylesheet" href="css/light-bootstrap-dashboard.css?v=2.0.1"/>
    <link rel="stylesheet" href="css/demo.css"/>
</head>

<body>
<div class="wrapper">
    <%@ include file="template/sidebar.jsp" %>
    <div class="main-panel">
        <%@ include file="template/navbar.jsp" %>

        <div class="content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-6 col-sm-6">
                        <div class="card card-stats">
                            <div class="card-body ">
                                <div class="row">
                                    <div class="col-5">
                                        <div class="icon-big text-center icon-warning">
                                            <i class="nc-icon nc-send text-warning"></i>
                                        </div>
                                    </div>
                                    <div class="col-7">
                                        <div class="numbers">
                                            <p class="card-category">Submitted</p>
                                            <h4 class="card-title"><span id="submitted"></span> article(s)</h4>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer ">
                                <hr>
                                <div class="stats"><i class="fa fa-refresh"></i>Update Now</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6 col-sm-6">
                        <div class="card card-stats">
                            <div class="card-body ">
                                <div class="row">
                                    <div class="col-5">
                                        <div class="icon-big text-center icon-warning">
                                            <i class="nc-icon nc-check-2 text-success"></i>
                                        </div>
                                    </div>
                                    <div class="col-7">
                                        <div class="numbers">
                                            <p class="card-category">Selected for publishing</p>
                                            <h4 class="card-title"><span id="chosen"></span> article(s)</h4>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer ">
                                <hr>
                                <div class="stats"><i class="fa fa-refresh"></i>Update Now</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6 col-sm-6">
                        <div class="card card-stats">
                            <div class="card-body ">
                                <div class="row">
                                    <div class="col-5">
                                        <div class="icon-big text-center icon-warning">
                                            <i class="nc-icon nc-time-alarm text-danger"></i>
                                        </div>
                                    </div>
                                    <div class="col-7">
                                        <div class="numbers">
                                            <p class="card-category">Submission deadline</p>
                                            <h4 id="first_deadline" class="card-title">2019-04-30</h4>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer ">
                                <hr>
                                <div class="stats">
                                    <i class="fa fa-clock-o"></i>
                                    <span class="py-name"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6 col-sm-6">
                        <div class="card card-stats">
                            <div class="card-body ">
                                <div class="row">
                                    <div class="col-5">
                                        <div class="icon-big text-center icon-warning">
                                            <i class="nc-icon nc-planet text-primary"></i>
                                        </div>
                                    </div>
                                    <div class="col-7">
                                        <div class="numbers">
                                            <p class="card-category">Publishing deadline</p>
                                            <h4 id="second_deadline" class="card-title">2019-05-20</h4>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer ">
                                <hr>
                                <div class="stats">
                                    <i class="fa fa-clock-o"></i>
                                    <span class="py-name"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
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
<script type="text/javascript" src="./js/dashboard.js"></script>

</html>
