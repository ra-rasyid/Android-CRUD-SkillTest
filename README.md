# UserApp - Android User Management & API Integration

Sebuah aplikasi Android sederhana yang mendemonstrasikan integrasi API eksternal menggunakan **Retrofit**, pengelolaan data lokal, dan kustomisasi antarmuka pengguna (UI). Aplikasi ini dikembangkan sebagai bagian dari portofolio pengembangan aplikasi mobile.

## 🚀 Fitur Utama
- **Integrasi API:** Mengambil data user secara real-time dari [JSONPlaceholder](https://jsonplaceholder.typicode.com/).
- **Manajemen Data Lokal:** Kemampuan untuk menambah data user secara manual dengan input nama, email, dan alamat.
- **Custom UI:** Penggunaan `RecyclerView` dengan card view yang rapi untuk menampilkan list user.
- **Dynamic Assets:** Integrasi avatar otomatis untuk setiap user menggunakan layanan `pravatar.cc`.

## 🛠️ Stack Teknologi
- **Bahasa Pemrograman:** Kotlin
- **Networking:** Retrofit 2 & GSON Converter
- **UI Components:** RecyclerView, CardView, Material Design
- **Android Manifest:** Konfigurasi izin internet dan `usesCleartextTraffic` untuk keamanan jaringan.

## ⚙️ Cara Menjalankan Project
1. Clone repository ini.
2. Buka project di **Android Studio**.
3. Pastikan koneksi internet aktif.
4. Klik **Run** untuk menjalankan di Emulator atau Real Device.

## 📝 Catatan Pengembangan
Aplikasi ini menangani tantangan khusus dalam *data mapping*, di mana field `address` dari API (berupa objek) disesuaikan agar tidak bentrok dengan field `address` manual (berupa string) menggunakan teknik *custom serialization* pada GSON untuk menghindari *Type Mismatch*.

---
**Developed by Ragil - Fresh Graduate Informatics**
