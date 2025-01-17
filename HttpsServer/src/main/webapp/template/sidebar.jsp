<%@ page import="edu.fpt.comp1640.model.user.Administrator" %>
<div class="sidebar" data-color="orange" data-image="./img/university-of-greenwich.jpg">
    <div class="sidebar-wrapper">
        <div class="logo">
            <a href="#" class="simple-text logo-mini">AM</a>
            <a href="#" class="simple-text logo-normal">Annual Magazine</a>
        </div>
        <div class="user">
            <div class="photo">
                <img src="../img/default-avatar.png"/>
            </div>
            <div class="info ">
                <a data-toggle="collapse" href="#collapseExample" class="collapsed">
                    <span><%= user == null ? "" : user.getName() %><b class="caret"></b></span>
                </a>
                <div class="collapse" id="collapseExample">
                    <ul class="nav">
                        <li>
                            <a class="profile-dropdown" href="#pablo">
                                <span class="sidebar-mini">MP</span>
                                <span class="sidebar-normal">My Profile</span>
                            </a>
                        </li>
                        <li>
                            <a class="profile-dropdown" href="#pablo">
                                <span class="sidebar-mini">EP</span>
                                <span class="sidebar-normal">Edit Profile</span>
                            </a>
                        </li>
                        <li>
                            <a class="profile-dropdown" href="#pablo">
                                <span class="sidebar-mini">S</span>
                                <span class="sidebar-normal">Settings</span>
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <ul class="nav">
            <li class="nav-item ">
                <a class="nav-link" href="${pageContext.request.contextPath}/dashboard.jsp">
                    <i class="nc-icon nc-chart-pie-35"></i>
                    <p>Dashboard</p>
                </a>
            </li>
            <% if (user.canSubmit()) {%>
            <li class="nav-item ">
                <a class="nav-link" href="${pageContext.request.contextPath}/upload.jsp">
                    <i class="nc-icon nc-simple-add"></i>
                    <p> New Submission</p>
                </a>
            </li>
            <%}%>

            <% if (user.canViewSubmission()) {%>
            <li class="nav-item ">
                <a class="nav-link" href="${pageContext.request.contextPath}/submissions.jsp">
                    <i class="nc-icon nc-bullet-list-67"></i>
                    <p> Submissions</p>
                </a>
            </li>
            <%}%>

            <% if (user.canViewStatistic()) {%>
            <li class="nav-item ">
                <a class="nav-link" href="${pageContext.request.contextPath}/statistic.jsp">
                    <i class="nc-icon nc-chart"></i>
                    <p> Statistic</p>
                </a>
            </li>
            <%}%>

            <% if (user instanceof Administrator) {%>
            <li class="nav-item ">
                <a class="nav-link" href="${pageContext.request.contextPath}/event.jsp">
                    <i class="nc-icon nc-settings-gear-64"></i>
                    <p> System Settings</p>
                </a>
            </li>
            <%} %>
        </ul>
    </div>
</div>
