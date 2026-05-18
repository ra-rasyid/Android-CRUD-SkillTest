package com.example.userapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: UserAdapter
    private var userList = mutableListOf<User>() // List untuk Halaman Utama
    private var cartList = mutableListOf<User>() // List untuk Keranjang Sementara

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi View
        val rvUsers = findViewById<RecyclerView>(R.id.rvUsers)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        val btnOpenCart = findViewById<FrameLayout>(R.id.btnOpenCart)

        rvUsers.layoutManager = LinearLayoutManager(this)

        // Adapter utama menggunakan data dari userList
        adapter = UserAdapter(userList,
            onEdit = { user, position -> showUserDialog(user, position) },
            onDelete = { position -> deleteUser(position) }
        )
        rvUsers.adapter = adapter

        fetchData() // Ambil data awal dari API

        // Listener Tombol
        fabAdd.setOnClickListener { showUserDialog(null, -1) }
        btnOpenCart.setOnClickListener { showCartDialog() }
    }

    private fun fetchData() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        api.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    userList.clear()
                    response.body()?.let { userList.addAll(it) }
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Gagal memuat data API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showUserDialog(user: User?, position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (user == null) "Tambah Pengguna Baru" else "Opsi Pengguna")

        // Setup Layout Dialog secara Programmatic
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(60, 40, 60, 10)

        val inputName = EditText(this).apply { hint = "Nama Lengkap"; setText(user?.name ?: "") }
        val inputEmail = EditText(this).apply { hint = "Alamat Email"; setText(user?.email ?: "") }
        val inputAddress = EditText(this).apply { hint = "Alamat Domisili"; setText(user?.address ?: "") }

        layout.addView(inputName)
        layout.addView(inputEmail)
        layout.addView(inputAddress)
        builder.setView(layout)

        // Tombol Positif: Langsung ke List Utama (Atau Update jika sedang Edit)
        builder.setPositiveButton(if (user == null) "Simpan ke Utama" else "Perbarui") { _, _ ->
            processInput(inputName.text.toString(), inputEmail.text.toString(), inputAddress.text.toString(), user, position, toCart = false)
        }

        if (user == null) {
            // Jika Tambah Baru: Ada opsi simpan ke Keranjang
            builder.setNeutralButton("Simpan ke Keranjang") { _, _ ->
                processInput(inputName.text.toString(), inputEmail.text.toString(), inputAddress.text.toString(), user, position, toCart = true)
            }
        } else {
            // Jika Edit: Ada opsi pindahkan data yang sudah ada ke Keranjang
            builder.setNeutralButton("Pindahkan ke Keranjang") { _, _ ->
                cartList.add(user)
                userList.removeAt(position)
                adapter.notifyDataSetChanged()
                updateCartBadge()
                Toast.makeText(this, "Data berhasil dipindahkan dari List Utama", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Batal", null)
        builder.show()
    }

    private fun processInput(name: String, email: String, address: String, user: User?, position: Int, toCart: Boolean) {
        if (name.isNotEmpty() && email.isNotEmpty()) {
            if (user == null) {
                // LOGIKA CREATE (Data Baru)
                val newUser = User(
                    id = (userList.size + cartList.size) + 1,
                    name = name,
                    email = email,
                    address = address
                )
                if (toCart) {
                    cartList.add(newUser)
                    updateCartBadge()
                    Toast.makeText(this, "Tersimpan di Keranjang Sementara", Toast.LENGTH_SHORT).show()
                } else {
                    userList.add(0, newUser)
                    adapter.notifyItemInserted(0)
                    Toast.makeText(this, "Berhasil ditambahkan ke List Utama", Toast.LENGTH_SHORT).show()
                }
            } else {
                // LOGIKA UPDATE (Data Lama)
                userList[position] = user.copy(name = name, email = email, address = address)
                adapter.notifyItemChanged(position)
                Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Nama dan Email wajib diisi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCartDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Daftar Sementara (${cartList.size} User)")

        if (cartList.isEmpty()) {
            builder.setMessage("Keranjang Anda masih kosong.")
            builder.setPositiveButton("Tutup", null)
        } else {
            // Tampilkan List Keranjang di dalam Dialog
            val rvCart = RecyclerView(this)
            rvCart.layoutManager = LinearLayoutManager(this)
            rvCart.adapter = UserAdapter(cartList, { _, _ -> }, { pos ->
                cartList.removeAt(pos)
                updateCartBadge()
                showCartDialog() // Refresh dialog untuk memperbarui list keranjang
            })
            builder.setView(rvCart)

            // Istilah "Checkout" diganti menjadi "Terbitkan Semua" agar lebih nyambung
            builder.setPositiveButton("Terbitkan Semua ke Utama") { _, _ ->
                userList.addAll(0, cartList)
                adapter.notifyDataSetChanged()
                cartList.clear()
                updateCartBadge()
                Toast.makeText(this, "Semua data berhasil diterbitkan", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("Tutup", null)
        }
        builder.show()
    }

    private fun updateCartBadge() {
        val tvCartCount = findViewById<TextView>(R.id.tvCartCount)
        tvCartCount.text = cartList.size.toString()
        tvCartCount.visibility = if (cartList.size > 0) View.VISIBLE else View.GONE
    }

    private fun deleteUser(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Data")
            .setMessage("Data ini akan dihapus permanen. Lanjutkan?")
            .setPositiveButton("Hapus") { _, _ ->
                userList.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}