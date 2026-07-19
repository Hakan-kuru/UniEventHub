# UniEventHub — Sprint Progress Report
**Sprint:** Super Admin User Management UI + Admin Assignment
**Tarih:** 2026-06-05
**Branch:** `admin_ui_v2`

---

## ✅ Sprint 2 — Tamamlananlar

### 1. AdminUserDto — Yeni Kotlin Data Class
- `data/remote/response/AdminUserDto.kt` oluşturuldu
- Backend `AdminUserDto.java` ile birebir map: `userId`, `name`, `email`, `role`, `universityId`

### 2. ApiService — getAllUsers() Endpoint
- `GET v1/admin/users` eklendi: `suspend fun getAllUsers(): Response<List<AdminUserDto>>`
- Tüm SuperAdmin endpoint'leri artık tam: assign / remove / list / users

### 3. SuperAdminRepository — Interface + Impl Güncellendi
- `domain/repository/SuperAdminRepository.kt`: `getAllUsers(): Result<List<AdminUserDto>>` eklendi
- `data/repository/SuperAdminRepositoryImpl.kt`: implementasyon eklendi, try/catch + error handling

### 4. SuperAdminViewModel — Tam Yeniden Yazım
**Önceki durum:** Sadece admin listesi + assign/remove vardı, kullanıcı listesi yoktu

**Yeni durum:**
- `_allUsers: MutableStateFlow<List<AdminUserDto>>` — backend'den tüm kullanıcılar
- `_searchQuery: MutableStateFlow<String>` — arama sözcüğü
- `filteredUsers: StateFlow<List<AdminUserDto>>` — combine() ile client-side filtreleme
- `adminCount: StateFlow<Int>` — KPI için admin sayısı (combine ile hesaplanır)
- `loadUsers()` — `/v1/admin/users` çağırır
- `fetchAdmins()` — `/v1/admin/list` çağırır (admin panel özeti)
- `assignAdmin(email, universityId)` — assign sonrası hem users hem admins refresh
- `removeAdmin(userId)` — remove sonrası hem users hem admins refresh
- `onSearchChange(query)` — arama sorgusunu günceller
- `resetState()` — snackbar sonrası state sıfırlanır

### 5. SuperAdminPanelScreen — Tam UI (encoding düzeltmesi dahil)
**Önceki durum:** Bozuk encoding (garbled Turkish chars), sadece assign formu + admin list

**Yeni durum:**
- **KPI Card:** Üstte `Toplam Admin` + `Toplam Kullanici` sayıları (primaryContainer rengi)
- **SearchBar:** Email ve isimle canlı filtreleme, Clear butonu
- **UserListCard:** Her kullanıcı için:
  - Ad + Email
  - `RoleBadge` (renk kodlu: SUPER_ADMIN=mor, ADMIN=mavi, USER=gri)
  - `Üniversite #ID` (admin ise)
  - `USER` → **"Admin Yap"** butonu (FilledTonalButton)
  - `ADMIN` → **"Kaldir"** butonu (errorContainer rengi)
  - `SUPER_ADMIN` → **"Korumalı"** (dokunulamaz chip)
- **AlertDialog:** "Admin Yap" tıklanınca universityId girişi için dialog açılır
- **Empty State:** Kullanıcı yoksa veya arama eşleşmesi yoksa açıklayıcı mesaj
- **Loading State:** CircularProgressIndicator (liste yüklenirken)
- **Snackbar:** Başarı/hata mesajları
- Tüm dosya UTF-8 ile yeniden yazıldı (encoding bozukluğu giderildi)

### 6. ExploreScreen — Gerçek Event Datası
**Önceki durum:** `"Yakında: Tüm etkinlikleri burada listeleyebileceksiniz."` stub text

**Yeni durum:**
- `ExploreViewModel.kt` oluşturuldu:
  - `eventRepository.getAllEvents()` çağrısı
  - `filteredEvents` — combine ile client-side title/clubName filtreleme
  - `isLoading`, `error` state'leri
  - `loadEvents()`, `onSearchChange()` fonksiyonları
- `ExploreScreen.kt` güncellendi:
  - SearchBar (etkinlik ve kulüp adıyla filtreleme)
  - `EventCard`: title, clubName, description (2 satır), startAt, capacity
  - Loading / Error / Empty state'leri
  - "Tekrar Dene" butonu (error state'de)

---

## 🔁 Veri Akışı (Sprint 2 Super Admin Paneli)

```
SuperAdminPanelScreen
    └─ SuperAdminViewModel (Hilt)
        ├─ loadUsers()      → SuperAdminRepository.getAllUsers()
        │                       → ApiService.GET /v1/admin/users
        │                           → Backend: SuperAdminController.getAllUsers()
        │                               → SuperAdminService.getAllUsers()
        ├─ assignAdmin()    → SuperAdminRepository.assignAdmin()
        │                       → ApiService.POST /v1/admin/assign
        │                           → Backend: SuperAdminService.assignAdminByEmail()
        └─ removeAdmin()    → SuperAdminRepository.removeAdmin()
                                → ApiService.DELETE /v1/admin/{userId}
                                    → Backend: SuperAdminService.removeAdmin()
```

---

## ⏳ Kalan Eksikler (Sprint 3 için)

| Özellik | Durum | Not |
|---------|-------|-----|
| `ClubDetail` ekranı | 🔴 Yok | Route tanımlı değil |
| `EventDetail` ekranı | 🔴 Yok | Route tanımlı değil |
| `AdminUniversity` yönetimi | 🔴 Yok | Backend endpoint var |
| DateTimePicker entegrasyonu | 🟡 Eksik | `ClubAdminCreateEventScreen`'de ISO-8601 manuel giriş |
| Push notification (FCM) | 🔴 Yok | Backend Spring Event altyapısı var |
| ProfileScreen gerçek data | 🟡 Kısmen | `ProfileViewModel` var, UI doğrulanmadı |
| Unit test coverage | 🔴 Yok | Hiçbir ViewModel/UseCase testi yok |
| Token yenileme (Refresh) | 🔴 Yok | OkHttp Authenticator eklenmeli |
| EventDetail — Join/Leave | 🔴 Yok | API endpoint'leri var (joinEvent/leaveEvent) |

---

## 🏗️ Sprint Durumu

```
Sprint 2 Toplam Madde  : 9
Tamamlanan             : 9 / 9  ✅
Kısmi                  : 0
Kalanlar               : Sprint 3'e devredildi
```

---

## 🔴 Kritik Teknik Notlar

1. **`SuperAdminViewModel.adminCount`** combine ile `_allUsers`'dan hesaplanıyor.
   Ayrı bir `/admin/list` çağrısı yapmadan mevcut kullanıcı listesinden türetiliyor.

2. **`filteredUsers` StateFlow:** `SharingStarted.WhileSubscribed(5_000)` ile
   screen recomposition'da gereksiz API çağrısı engelleniyor.

3. **`assignAdmin` Dialog:** UniversityId girişi şimdi ana form'dan çıkarılıp
   bir AlertDialog'a taşındı — LIST üzerindeki "Admin Yap" butonuyla tetikleniyor.
   Bu daha temiz UX sağlıyor.

4. **`ExploreViewModel`** navigation package altında — ileride `presentation/explore/`
   package'ına taşınması önerilir (ClubsScreen pattern'i gibi).

5. **Backend `findAll()`:** `SuperAdminService.getAllUsers()` içinde `userRepository.findAll()`
   kullanılıyor — büyük kullanıcı tabanında pagination eklenmeli (Pageable).