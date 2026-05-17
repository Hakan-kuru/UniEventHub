package com.hakankuru.eventhub.presentation.navigation

sealed class Route(val route: String) {
    // Auth
    object Login : Route("login")
    object Register : Route("register")
    object AdminLogin : Route("admin_login")

    // User App (Bottom Nav)
    object Home : Route("home")
    object Explore : Route("explore")
    object Clubs : Route("clubs")
    object Profile : Route("profile")
    
    // User App (Details)
    object ClubDetail : Route("club_detail/{clubId}") {
        fun createRoute(clubId: Long) = "club_detail/$clubId"
    }
    object EventDetail : Route("event_detail/{eventId}") {
        fun createRoute(eventId: Long) = "event_detail/$eventId"
    }

    // Admin Panel
    object AdminDashboard : Route("admin_dashboard")
    object AdminUniversity : Route("admin_university")
    object AdminClubs : Route("admin_clubs")
    object AdminUsers : Route("admin_users")
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    // Note: We'll use material icons in the actual implementation
) {
    object Home : BottomNavItem(Route.Home.route, "Ana Sayfa")
    object Explore : BottomNavItem(Route.Explore.route, "Keşfet")
    object Clubs : BottomNavItem(Route.Clubs.route, "Kulüpler")
    object Profile : BottomNavItem(Route.Profile.route, "Profil")
}
