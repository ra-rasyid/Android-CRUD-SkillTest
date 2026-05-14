package com.example.userapp

import android.os.Bundle
import android.util.Log
import android.widget.EditText
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
    private var userList = mutableListOf<User>() // List lokal untuk menampung data CRUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi View
        val rvUsers = findViewById<RecyclerView>(R.id.rvUsers)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        rvUsers.layoutManager = LinearLayoutManager(this)

        // Inisialisasi Adapter dengan Lambda Functions untuk Edit dan Delete
        adapter = UserAdapter(userList,
            onEdit = { user, position -> showUserDialog(user, position) },
            onDelete = { position -> deleteUser(position) }
        )
        rvUsers.adapter = adapter

        // Ambil data awal dari API
        fetchData()

        // Tombol Tambah (Create)
        fabAdd.setOnClickListener {
            showUserDialog(null, -1)
        }
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
                    Log.d("API_SUCCESS", "Berhasil memuat ${userList.size} data")
                } else {
                    Log.e("API_ERROR", "Error Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("API_FAILURE", "Pesan Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Gagal terhubung ke API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Fungsi Gabungan untuk Create dan Update menggunakan AlertDialog
    private fun showUserDialog(user: User?, position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (user == null) "Tambah User Baru" else "Edit Nama User")

        val input = EditText(this)
        input.hint = "Masukkan Nama"
        input.setText(user?.name ?: "")
        builder.setView(input)

        builder.setPositiveButton("Simpan") { _, _ ->
            val newName = input.text.toString()
            if (newName.isNotEmpty()) {
                if (user == null) {
                    // Logika CREATE
                    val newUser = User(
                        id = userList.size + 1,
                        name = newName,
                        email = "user${userList.size + 1}@mail.com"
                    )
                    userList.add(0, newUser) // Tambah ke posisi paling atas
                    adapter.notifyItemInserted(0)
                    Toast.makeText(this, "User berhasil ditambah", Toast.LENGTH_SHORT).show()
                } else {
                    // Logika UPDATE (Perbaikan Crash NullPointerException)
                    userList[position] = user.copy(name = newName)
                    adapter.notifyItemChanged(position)
                    Toast.makeText(this, "Nama berhasil diubah", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Batal", null)
        builder.show()
    }

    // Fungsi DELETE
    private fun deleteUser(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Hapus User")
            .setMessage("Apakah Anda yakin ingin menghapus user ini?")
            .setPositiveButton("Ya") { _, _ ->
                userList.removeAt(position)
                adapter.notifyItemRemoved(position)
                Toast.makeText(this, "User telah dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}