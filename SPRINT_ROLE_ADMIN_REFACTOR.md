# Sprint: Role & Admin System Refactor — Uygulama Raporu

**Tarih:** 2026-06-04  
**Branch:** admin_ui_v2  
**Kapsam:** Backend (Spring Boot) + Android (Jetpack Compose / Clean Architecture)

---

## ? Yapýlan Deðiþiklikler

---

### ?? BACKEND (EventTime — Spring Boot)

#### 1. GlobalRole.java — ADMIN rolü eklendi
- Enum'a ADMIN deðeri eklendi.
- Artýk üç rol var: USER, ADMIN, SUPER_ADMIN.

#### 2. UserRole.java — university alaný eklendi
- users_roles tablosuna university_id (nullable) FK kolonu eklendi.
- @ManyToOne / @JoinColumn(name = "university_id", nullable = true) ile University entity'sine baðlandý.
- Kural: SUPER_ADMIN › NULL, ADMIN › ilgili üniversite.

#### 3. UserRoleRepository.java — yeni dosya
- indByUser_UserId(Long userId) — kullanýcýnýn tüm rollerini getirir.
- indByUser_UserIdAndRole(Long userId, String role) — belirli rolü getirir.
- indByRole(String role) — tüm ADMIN'leri listelemek için kullanýlýr.

#### 4. AssignAdminRequest.java — yeni DTO
- email + universityId alanlarýný içerir.
- SuperAdmin'in POST /api/v1/admin/assign endpoint'ine gönderdiði body.

#### 5. AdminResponse.java — yeni DTO
- userId, email, ole, universityId alanlarý.
- Admin atama/listeleme iþlemlerinin response modeli.

#### 6. UserProfileResponse.java — universityId alaný eklendi
- Long universityId (nullable) alaný eklendi.
- ADMIN rolündeki kullanýcýlar için hangi üniversiteyi yönettiðini taþýr.
- SUPER_ADMIN ve USER için 
ull döner.

#### 7. UserService.java — universityId hesaplama eklendi
- UserRoleRepository inject edildi.
- getUserProfile() içinde: kullanýcý ADMIN ise users_roles tablosundan university_id çekilerek response'a ekleniyor.

#### 8. SuperAdminService.java — yeni servis
- ssignAdminByEmail(AssignAdminRequest):
  - Email ile User bulur.
  - universityId ile University bulur.
  - users_roles tablosunda var olan ADMIN kaydýný override eder ya da yeni oluþturur.
  - User.globalRole deðerini ADMIN olarak günceller.
  - Ýþ kuralý: ayný user birden fazla ADMIN olamaz — override yapýlýr.
- emoveAdmin(Long userId):
  - users_roles'dan ADMIN kaydýný siler.
  - User.globalRole'ü USER'a çevirir.
- getAllAdmins():
  - users_roles tablosundan ole = ADMIN olan tüm kayýtlarý listeler.

#### 9. SuperAdminController.java — yeni controller
- POST /api/v1/admin/assign › @PreAuthorize("hasRole('SUPER_ADMIN')")
- DELETE /api/v1/admin/{userId} › @PreAuthorize("hasRole('SUPER_ADMIN')")
- GET /api/v1/admin/list › @PreAuthorize("hasRole('SUPER_ADMIN')")

---

### ?? ANDROID (eventhub — Jetpack Compose)

#### 1. UserProfileResponse.kt — universityId alaný eklendi
- al universityId: Long? = null eklendi.
- Backend ile birebir eþleþen nullable alan.
- Retrofit JSON deserializasyonunda backward compat için default 
ull.

#### 2. RoleGuard.kt — yeni fonksiyonlar eklendi
- isAdmin(profile) › globalRole == "ADMIN" kontrolü.
- isAdminOfUniversity(profile, universityId):
  - SUPER_ADMIN › her üniversiteye eriþebilir.
  - ADMIN › sadece kendi universityId'si ile eþleþen üniversiteye eriþebilir.
- Mevcut isSuperAdmin, isClubAdmin, isClubMember, dminClubIds, memberClubIds korundu.

#### 3. AssignAdminRequest.kt — yeni request modeli
- email: String + universityId: Long içerir.
- Backend AssignAdminRequest DTO ile birebir eþleþir.

#### 4. AdminResponse.kt — yeni response modeli
- userId, email, ole, universityId: Long? içerir.
- Backend AdminResponse DTO ile birebir eþleþir.

#### 5. ApiService.kt — 3 yeni endpoint eklendi
- POST v1/admin/assign › ssignAdmin(AssignAdminRequest): Response<AdminResponse>
- DELETE v1/admin/{userId} › emoveAdmin(userId): Response<Unit>
- GET v1/admin/list › getAllAdmins(): Response<List<AdminResponse>>
- Kullanýlmayan import'lar temizlendi, @DELETE / @Path annotation'larý düzgün eklendi.

#### 6. SuperAdminRepository.kt (domain interface) — güncellendi
- ssignAdmin(AssignAdminRequest): Result<AdminResponse> eklendi.
- emoveAdmin(userId: Long): Result<Unit> eklendi.
- getAllAdmins(): Result<List<AdminResponse>> eklendi.

#### 7. SuperAdminRepositoryImpl.kt — güncellendi
- Yukarýdaki 3 yeni interface metodunun implementasyonu yazýldý.
- Mevcut club metodlarý korundu.

#### 8. SuperAdminViewModel.kt — yeni ViewModel
- SuperAdminUiState sealed class (Initial / Loading / Success / Error).
- dmins: StateFlow<List<AdminResponse>> — admin listesini tutar.
- isLoading: StateFlow<Boolean> — liste yüklenirken spinner için.
- etchAdmins() — init bloðunda otomatik çaðrýlýr.
- ssignAdmin(email, universityId) — atama yapar, sonrasýnda listeyi yeniler.
- emoveAdmin(userId) — kaldýrýr, sonrasýnda listeyi yeniler.
- esetState() — Snackbar gösterdikten sonra state'i temizler.

#### 9. SuperAdminPanelScreen.kt — yeni ekran
- OutlinedTextField › Email input.
- OutlinedTextField › Üniversite ID input (numeric).
- **"Admin Yap"** butonu › iewModel.assignAdmin() çaðýrýr.
- Loading state sýrasýnda buton disable + spinner gösterir.
- LazyColumn › Mevcut adminleri listeler (AdminListItem card).
- Her card'da: email, üniversite ID, ve saðda **delete icon** (iewModel.removeAdmin()).
- Snackbar ile baþarý/hata mesajlarý gösterilir.
- Scaffold içinde TopAppBar + geri butonu.

#### 10. Routes.kt — SuperAdminPanel route eklendi
- object SuperAdminPanel : Route("super_admin_panel")
- Eski TODO comment'ler kaldýrýldý.

#### 11. EventHubApp.kt — güncellendi
- SuperAdminPanelScreen import eklendi.
- AdminDashboardScreen composable çaðrýsýna onNavigateToSuperAdminPanel callback eklendi.
- SuperAdminPanel için yeni composable(Route.SuperAdminPanel.route) bloðu eklendi.
  - Guard: RoleGuard.isSuperAdmin(profile) › false ise UnauthorizedScreen gösterilir.

#### 12. AdminDashboardScreen.kt — güncellendi
- onNavigateToSuperAdminPanel: () -> Unit = {} parametresi eklendi.
- **"Admin Yönetimi (Admin Ata / Kaldýr)"** butonu eklendi (secondary renk).
- Buton onNavigateToSuperAdminPanel callback'ini tetikler.

---

## ?? Veri Akýþý (End-to-End)

`
SuperAdmin giriþ yapar
  › JWT alýr (SUPER_ADMIN rolü)
  › /me API çaðrýlýr › SessionViewModel güncellenir
  › AdminDashboardScreen açýlýr (RoleGuard.isSuperAdmin ?)
  › "Admin Yönetimi" butonuna basar
  › SuperAdminPanelScreen açýlýr
  › Email + UniversityID girer › "Admin Yap" butonuna basar
  › SuperAdminViewModel.assignAdmin()
  › SuperAdminRepositoryImpl.assignAdmin()
  › POST /api/v1/admin/assign (Backend)
  › SuperAdminService.assignAdminByEmail()
  › users_roles tablosu güncellenir (university_id set edilir)
  › User.globalRole = ADMIN olur
  › Liste otomatik yenilenir (fetchAdmins())
`

---

## ?? Kritik Kurallar (Uygulandý)

| Kural | Durum |
|---|---|
| Backend ve Android universityId mapping birebir ayný | ? |
| Rol kontrolü JWT deðil /me üzerinden yapýlýyor | ? |
| users_roles SINGLE SOURCE OF TRUTH | ? |
| Ayný user birden fazla ADMIN olamaz (override) | ? |
| SUPER_ADMIN university_id = NULL | ? |
| ADMIN kaldýrýldýðýnda globalRole = USER yapýlýyor | ? |
| SUPER_ADMIN guard: tüm admin endpoint'lerinde @PreAuthorize | ? |
| Android'de UnauthorizedScreen guard her iki yeni route'da | ? |

---

## ?? Deðiþtirilen / Oluþturulan Dosyalar

### Backend
| Dosya | Ýþlem |
|---|---|
| entity/GlobalRole.java | Güncellendi — ADMIN eklendi |
| entity/UserRole.java | Güncellendi — university FK eklendi |
| epository/UserRoleRepository.java | **Yeni** |
| dto/AssignAdminRequest.java | **Yeni** |
| dto/AdminResponse.java | **Yeni** |
| dto/UserProfileResponse.java | Güncellendi — universityId eklendi |
| service/UserService.java | Güncellendi — universityId hesaplama |
| service/SuperAdminService.java | **Yeni** |
| controller/SuperAdminController.java | **Yeni** |

### Android
| Dosya | Ýþlem |
|---|---|
| esponse/UserProfileResponse.kt | Güncellendi — universityId eklendi |
| model/RoleGuard.kt | Güncellendi — isAdmin, isAdminOfUniversity |
| equest/AssignAdminRequest.kt | **Yeni** |
| esponse/AdminResponse.kt | **Yeni** |
| emote/ApiService.kt | Güncellendi — 3 endpoint eklendi |
| epository/SuperAdminRepository.kt | Güncellendi — 3 metod eklendi |
| epository/SuperAdminRepositoryImpl.kt | Güncellendi — 3 impl eklendi |
| dmin/SuperAdminViewModel.kt | **Yeni** |
| dmin/SuperAdminPanelScreen.kt | **Yeni** |
| 
avigation/Routes.kt | Güncellendi — SuperAdminPanel route |
| 
avigation/EventHubApp.kt | Güncellendi — route + import |
| dmin/AdminDashboardScreen.kt | Güncellendi — yeni buton + param |
