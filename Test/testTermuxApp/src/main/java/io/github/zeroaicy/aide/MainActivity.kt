package io.github.zeroaicy.aide

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.termux.app.TermuxActivity


class MainActivity : AppCompatActivity() {

    // 这是Termux原来的MainActivity
    var termuxActivity: TermuxActivity? = null

    /**
     * 你可以继承 TermuxActivity
     * 也可以通过外部启用TermuxActivity
     * 高度灵活
     */


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        startActivity(Intent(this, TermuxActivity::class.java))

    }
}