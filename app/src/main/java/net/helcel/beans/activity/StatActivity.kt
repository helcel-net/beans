package net.helcel.beans.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beans.R
import net.helcel.beans.activity.adapter.StatsListAdapter
import net.helcel.beans.databinding.ActivityStatBinding
import net.helcel.beans.helper.Theme.createActionBar


class StatActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityStatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStatBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        createActionBar(this, getString(R.string.action_stat))


        _binding.stats.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        _binding.stats.adapter = StatsListAdapter()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }


    private fun bind() {
    }
}