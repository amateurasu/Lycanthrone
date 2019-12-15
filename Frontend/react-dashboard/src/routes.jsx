/*!
 * Material Dashboard React - v1.8.0
 */

import {
    BubbleChart, Dashboard, Language, LibraryBooks, LocationOn, Notifications, Person, Unarchive
} from "@material-ui/icons";

import DashboardPage from "./views/Dashboard/Dashboard";
import UserProfile from "./views/UserProfile/UserProfile";
import TableList from "./views/TableList/TableList";
import Typography from "./views/Typography/Typography";
import Icons from "./views/Icons/Icons";
import Maps from "./views/Maps/Maps";
import NotificationsPage from "./views/Notifications/Notifications";
import UpgradeToPro from "./views/UpgradeToPro/UpgradeToPro";

import RTLPage from "./views/RTLPage/RTLPage";

const dashboardRoutes = [{
        path: "/dashboard",
        name: "Dashboard",
        rtlName: "لوحة القيادة",
        icon: Dashboard,
        component: DashboardPage,
        layout: "/admin"
    }, {
        path: "/user",
        name: "User Profile",
        rtlName: "ملف تعريفي للمستخدم",
        icon: Person,
        component: UserProfile,
        layout: "/admin"
    }, {
        path: "/table",
        name: "Table List",
        rtlName: "قائمة الجدول",
        icon: "content_paste",
        component: TableList,
        layout: "/admin"
    }, {
        path: "/typography",
        name: "Typography",
        rtlName: "طباعة",
        icon: LibraryBooks,
        component: Typography,
        layout: "/admin"
    }, {
        path: "/icons",
        name: "Icons",
        rtlName: "الرموز",
        icon: BubbleChart,
        component: Icons,
        layout: "/admin"
    }, {
        path: "/maps",
        name: "Maps",
        rtlName: "خرائط",
        icon: LocationOn,
        component: Maps,
        layout: "/admin"
    }, {
        path: "/notifications",
        name: "Notifications",
        rtlName: "إخطارات",
        icon: Notifications,
        component: NotificationsPage,
        layout: "/admin"
    }, {
        path: "/rtl-page",
        name: "RTL Support",
        rtlName: "پشتیبانی از راست به چپ",
        icon: Language,
        component: RTLPage,
        layout: "/rtl"
    }, {
        path: "/upgrade-to-pro",
        name: "Upgrade To PRO",
        rtlName: "التطور للاحترافية",
        icon: Unarchive,
        component: UpgradeToPro,
        layout: "/admin"
    }
];

export default dashboardRoutes;
