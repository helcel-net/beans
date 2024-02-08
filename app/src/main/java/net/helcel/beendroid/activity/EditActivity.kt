package net.helcel.beendroid.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beendroid.R
import net.helcel.beendroid.countries.World
import net.helcel.beendroid.helper.createActionBar
import net.helcel.beendroid.helper.visited


class EditActivity : AppCompatActivity() {

    private lateinit var list : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit)
        createActionBar(this, getString(R.string.action_edit))

        list = findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        list.adapter = FoldingListAdapter(this, World.WWW.children, visited!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Configure on back pressed
        finish()
        return super.onOptionsItemSelected(item)
    }


}