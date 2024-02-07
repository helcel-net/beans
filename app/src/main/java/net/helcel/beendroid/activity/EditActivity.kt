package net.helcel.beendroid.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beendroid.R
import net.helcel.beendroid.countries.Visited
import net.helcel.beendroid.countries.World


class EditActivity : AppCompatActivity() {

    private lateinit var list : RecyclerView
    private lateinit var visited : Visited

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit)

        visited = Visited(this)
        visited.load()

        list = findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        list.adapter = FoldingListAdapter(this, World.WWW.children, visited) {  }
    }

}