# UniEventHub — Sprint Progress Report
**Sprint:** Real Data Integration & Role Stabilization
**Tarih:** 2026-06-02
**Branch:** `feature/role-based-admin-ui`

---

## ✅ Bu Sprintte Tamamlananlar

### 1. RoleGuard — Merkezi Yetki Sistemi
- `domain/model/RoleGuard.kt` oluşturuldu
- `isSuperAdmin()`, `isClubAdmin()`, `isClubMember()`, `adminClubIds()`, `memberClubIds()` fonksiyonları
- UI içinde artık hiçbir yerde string karşılaştırması (`== "ADMIN"`) yapılmıyor

### 2. SessionViewModel — Bootstrap Sistemi
- `presentation/ui/session/SessionViewModel.kt` oluşturuldu
- App açılışında token kontrolü → `/me` çağrısı otomatik yapılıyor
- `SessionState`: `Loading | NoSession | Authenticated(profile) | Error`
- Tüm ekranlar profile bilgisini bu state üzerinden okuyor
- `EventHubApp` içine `hiltViewModel<SessionViewModel>()` ile entegre edildi

### 3. ClubsScreen — Mock'lardan Temizlendi, Gerçek Data
- **Kaldırılan:** `isClubMember = true`, `isClubAdmin = true`, `onNavigateToCreateEvent(1L)` hardcode'ları
- **Eklenen:** `ClubsViewModel` (yeni dosya)
  - `userRepository.getCurrentUser()` + `clubRepository.getAllClubs()` paralel çağrı
  - `ClubsUiState`: `Loading | Success(clubs, profile) | Error`
- Her kulüp kartı, `RoleGuard`'dan gelen gerçek role'e göre render ediliyor
- Admin'e özel butonlar (Etkinlik Ekle, Üyeler) yalnızca gerçek admin'e görünüyor
- Loading ve Error state'leri UI'da gösteriliyor

### 4. AdminDashboard — Statik Veriler Kaldırıldı
- `AdminSuperViewModel` yeniden yazıldı:
  - `loadDashboard()` → `superAdminRepository.getAllClubs()` ile gerçek sayıları çekiyor
  - `AdminDashboardData(clubCount, clubs)` domain modeli eklendi
  - `createClub()` başarılı olduktan sonra dashboard otomatik yenileniyor
- `AdminDashboardScreen`:
  - Sahte sayılar (`"1,245"`, `"42"`) kaldırıldı
  - Gerçek `dashboardData` StateFlow'a bağlandı
  - Loading indicator eklendi
  - Snackbar ile hata/başarı mesajları
  - `AdminSuperViewModel`'e tam entegrasyon sağlandı

### 5. AdminLoginScreen — GlobalRole Koruması
- Artık gerçek `AuthViewModel.login()` kullanıyor
- Login başarılı olsa bile `authResponse.role != "SUPER_ADMIN"` ise admin paneline izin verilmiyor
- Role hatası kullanıcıya açık mesajla gösteriliyor
- PasswordVisualTransformation eklendi

### 6. AuthResponse — role Alanı Eklendi
- `data/remote/request/AuthResponse.kt`'ye `role: String?` alanı eklendi
- Backend'den gelen GlobalRole bilgisi artık client'ta kullanılabilir

### 7. ClubRepository — getAllClubs Eklendi
- `domain/repository/ClubRepository.kt`'ye `getAllClubs()` eklendi
- `data/repository/ClubRepositoryImpl.kt`'ye implementasyonu yazıldı
- `ClubsViewModel` bu metodla kulüp listesini kendi başına çekiyor

### 8. Navigation — Route Guard + Cleanup
- `EventHubApp.kt` tamamen yeniden yazıldı:
  - `SessionViewModel` entegre edildi
  - Admin ve ClubAdmin route'larına `RoleGuard` koruması eklendi
  - Yetkisiz erişimde `UnauthorizedScreen` gösteriliyor
  - Explore route'u artık `Text("TODO")` değil, gerçek `ExploreScreen()` composable'ı
  - Bottom nav'daki `Explore` ikonu `Icons.Filled.List` → `Icons.Filled.Search` olarak düzeltildi
  - Login başarısında `sessionViewModel.fetchProfile()` çağrılıyor
- Orphan `presentation/ui/screen/HomeScreen.kt` **silindi**
- `Routes.kt` temizlendi: kullanılmayan `AdminUniversity`, `AdminClubs`, `AdminUsers` route'ları kaldırıldı

### 9. Yeni Screen'ler
- `ExploreScreen.kt` — artık stub `Text()` değil, proper Composable
- `UnauthorizedScreen.kt` — yetkisiz erişimlerde gösterilen ekran

---

## ⏳ Kalan Eksikler (Sprint 2 için)

| Özellik | Durum | Not |
|---------|-------|-----|
| `ExploreScreen` gerçek event listesi | 🔴 Stub | `EventRepository.getAllEvents()` hazır, UI bağlanmadı |
| `ClubDetail` ekranı | 🔴 Yok | Route tanımlı değil — Sprint 2 |
| `EventDetail` ekranı | 🔴 Yok | Route tanımlı değil — Sprint 2 |
| `AdminUniversity` yönetimi | 🔴 Yok | Backend endpoint'i var |
| `AdminUsers` yönetimi | 🔴 Yok | Backend endpoint'i var |
| DateTimePicker entegrasyonu | 🟡 Eksik | `ClubAdminCreateEventScreen`'de ISO-8601 string manuel girişi |
| Push notification (FCM) | 🔴 Yok | Backend Spring Event altyapısı var |
| ProfileScreen gerçek data | 🟡 Kısmen | `ProfileViewModel` var, UI doğrulanmadı |
| Unit test coverage | 🔴 Yok | Hiçbir ViewModel/UseCase testi yok |

---

## 🏗️ Sprint Durumu

```
Toplam Madde   : 6
Tamamlanan     : 6 / 6  ✅
Kısmi          : 0
Kalanlar       : Sonraki sprint
```

---

## 🔴 Kritik Teknik Notlar

1. **`AuthResponse.role` backend uyumu:** Backend `AuthResponse` DTO'sunun da `role` alanını dönmesi
   gerekiyor. Spring tarafında `AuthResponse.java`'ya `globalRole` alanı eklenmeli. Field adı
   `role` olarak normalize edildi (mobil `role`, backend `globalRole` dönüyorsa Gson `@SerializedName("globalRole")` eklenebilir).

2. **`SessionViewModel` Hilt scope:** `EventHubApp` composable'ında `hiltViewModel()` ile
   oluşturuluyor — Activity scope'ta yaşatmak için `MainActivity`'de `viewModels()` ile çekilip
   parametre olarak geçirilmesi önerilir; aksi halde recomposition'da yeniden oluşabilir.

3. **`ClubRepository.getAllClubs()` vs `SuperAdminRepository.getAllClubs()`:** Her iki repository
   da aynı `GET v1/clubs` endpoint'ini çağırıyor. İleride `ClubsViewModel` ile `AdminSuperViewModel`
   aynı veriyi paylaştığında cache/single-source-of-truth pattern'i düşünülmeli.

4. **Token yenileme (Refresh) yok:** Mevcut `RetrofitClient` JWT'yi header'a ekliyor ancak token
   süresi dolduğunda otomatik yenileme mekanizması bulunmuyor. OkHttp `Authenticator` eklenmeli.
