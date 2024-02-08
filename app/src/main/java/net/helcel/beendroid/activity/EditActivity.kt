package net.helcel.beendroid.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beendroid.R
import net.helcel.beendroid.activity.fragment.AboutFragment
import net.helcel.beendroid.activity.fragment.LicenseFragment
import net.helcel.beendroid.activity.fragment.SettingsFragment
import net.helcel.beendroid.countries.Visited
import net.helcel.beendroid.countries.World
import net.helcel.beendroid.helper.colorPrimary


class EditActivity : AppCompatActivity() {

    private lateinit var list : RecyclerView
    private lateinit var visited : Visited

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit)

        // Create action bar
        supportActionBar?.setBackgroundDrawable(colorPrimary(this))
        supportActionBar?.title = getString(R.string.action_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        visited = Visited(this)
        visited.load()

        list = findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        list.adapter = FoldingListAdapter(this, World.WWW.children, visited) {  }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Configure on back pressed
        finish()
        return super.onOptionsItemSelected(item)
    }


}