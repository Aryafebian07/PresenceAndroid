package com.niveon.tugasakhir

import AdminHomeFragment
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.niveon.tugasakhir.databinding.ActivityMainBinding
import com.niveon.tugasakhir.ui.admin.profile.AdminProfileFragment
import com.niveon.tugasakhir.ui.dosen.home.DosenHomeFragment
import com.niveon.tugasakhir.ui.dosen.jadwal.DosenJadwalFragment
import com.niveon.tugasakhir.ui.dosen.notification.DosenNotificationFragment
import com.niveon.tugasakhir.ui.dosen.profile.DosenProfileFragment
import com.niveon.tugasakhir.ui.mahasiswa.home.MahasiswaHomeFragment
import com.niveon.tugasakhir.ui.mahasiswa.jadwal.MahasiswaJadwalFragment
import com.niveon.tugasakhir.ui.mahasiswa.notification.MahasiswaNotificationFragment
import com.niveon.tugasakhir.ui.mahasiswa.profile.MahasiswaProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.bottomNavigationView.setBackground(null)
        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            val fragment: Fragment? = when (menuItem.itemId) {
                R.id.admin_home -> {
                    binding.toolbar.visibility = View.GONE
                    AdminHomeFragment()
                }
                R.id.admin_profile -> {
                    binding.toolbar.visibility = View.GONE
                    AdminProfileFragment()
                }
                R.id.dosen_home -> {
                    binding.toolbar.visibility = View.GONE
                    DosenHomeFragment()
                }
//                R.id.dosen_jadwal -> {
//                    setToolbarTitle("Jadwal")
//                    DosenJadwalFragment()
//                }
//                R.id.dosen_notification -> {
//                    setToolbarTitle("Notification")
//                    DosenNotificationFragment()
//                }
                R.id.dosen_profile -> {
                    binding.toolbar.visibility = View.GONE
                    DosenProfileFragment()
                }
                R.id.mahasiswa_home -> {
                    binding.toolbar.visibility = View.GONE  
                    MahasiswaHomeFragment()
                }
//                R.id.mahasiswa_jadwal -> {
//                    setToolbarTitle("Jadwal")
//                    MahasiswaJadwalFragment()
//                }
//                R.id.mahasiswa_notification -> {
//                    setToolbarTitle("Notification")
//                    MahasiswaNotificationFragment()
//                }
                R.id.mahasiswa_profile -> {
                    binding.toolbar.visibility = View.GONE
                    MahasiswaProfileFragment()
                }
                else -> null
            }
            if (fragment != null) {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit()
                true
            } else {
                false
            }
        }

        val userRole = getUserRole()
        when (userRole) {
            "admin" -> {
                binding.toolbar.visibility = View.GONE
                setupAdminMenu()
                navigateToFragment(AdminHomeFragment())
            }
            "mahasiswa" -> {
                binding.toolbar.visibility = View.GONE
                binding.fab.setImageResource(R.drawable.ic_round_nfc)
                setupMahasiswaMenu()
                navigateToFragment(MahasiswaHomeFragment())
            }
            "dosen" -> {
                binding.toolbar.visibility = View.GONE
                setupDosenMenu()
                navigateToFragment(DosenHomeFragment())
            }

        }
    }

    private fun setupAdminMenu() {
        bottomNavigationView.menu.clear()
        bottomNavigationView.inflateMenu(R.menu.bottom_menu_admin)
    }

    private fun setupMahasiswaMenu() {
        bottomNavigationView.menu.clear()
        bottomNavigationView.inflateMenu(R.menu.bottom_menu_mahasiswa)
    }

    private fun setupDosenMenu() {
        binding.fab.setOnClickListener {
            showBottomDialog()
        }
        bottomNavigationView.menu.clear()
        bottomNavigationView.inflateMenu(R.menu.bottom_menu_dosen)
    }

    private fun getUserRole(): String? {
        return intent.getStringExtra("userRole")
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    fun setToolbarTitle(title: String) {
        val toolbarTitle = binding.toolbar.findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = title
        binding.toolbar.visibility = View.VISIBLE
    }

    fun showBottomDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlayout)

        val pertemuanLayout = dialog.findViewById<LinearLayout>(R.id.layoutPertemuan)
        val cancelButton = dialog.findViewById<ImageView>(R.id.cancelButton)

        pertemuanLayout.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this@MainActivity, "Tambah Pertemuan clicked", Toast.LENGTH_SHORT).show()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

    }
}
