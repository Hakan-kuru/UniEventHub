package com.hakankuru.eventhub.presentation.navigation

sealed class Route(val route: String) {
    // ── Auth ──────────────────────────────────────────────────────────────
    object Login      : Route("login")
    object Register   : Route("register")
    object AdminLogin : Route("admin_login")

    // ── User App (Bottom Nav) ──────────────────────────────────────────────
    object Home    : Route("home")
    object Explore : Route("explore")
    object Clubs   : Route("clubs")
    object Profile : Route("profile")

    // ── Admin Panel ────────────────────────────────────────────────────────
    object AdminDashboard : Route("admin_dashboard")
    object AdminCreateClub : Route("admin_create_club")
    // TODO – Sprint 2: implement aşağıdaki route'lar
    // object AdminUniversity : Route("admin_university")
    // object AdminUsers      : Route("admin_users")

    // ── Club Admin ─────────────────────────────────────────────────────────
    object ClubAdminCreateEvent : Route("club_admin_create_event/{clubId}") {
        fun createRoute(clubId: Long) = "club_admin_create_event/$clubId"
    }
    object ClubAdminMembers : Route("club_admin_members/{clubId}") {
        fun createRoute(clubId: Long) = "club_admin_members/$clubId"
    }
}

sealed class BottomNavItem(val route: String, val title: String) {
    object Home    : BottomNavItem(Route.Home.route,    "Ana Sayfa")
    object Explore : BottomNavItem(Route.Explore.route, "Keşfet")
    object Clubs   : BottomNavItem(Route.Clubs.route,   "Kulüpler")
    object Profile : BottomNavItem(Route.Profile.route, "Profil")
}
