package com.example.househunter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ContentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contents)

        findViewById<View>(R.id.logo).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val content1TextView: TextView = findViewById(R.id.content1)
        val content2TextView: TextView = findViewById(R.id.content2)
        val content3TextView: TextView = findViewById(R.id.content3)
        val content4TextView: TextView = findViewById(R.id.content4)
        val content5TextView: TextView = findViewById(R.id.content5)
        val content6TextView: TextView = findViewById(R.id.content6)
        val content7TextView: TextView = findViewById(R.id.content7)

        content1TextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://post.naver.com/viewer/postView.naver?volumeNo=29572083&memberNo=5113437"))
            startActivity(intent)
        }

        content2TextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.naver.com/wave1619/223360816573"))
            startActivity(intent)
        }

        content3TextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.naver.com/keaijinjin/223376940909"))
            startActivity(intent)
        }

        content4TextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://post.naver.com/viewer/postView.naver?volumeNo=33077720&memberNo=5113437&navigationType=push"))
            startActivity(intent)
        }

        content5TextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://brunch.co.kr/@e24e25be76964f4/44"))
            startActivity(intent)
        }

        content6TextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.naver.com/kimgr1010/223308098047"))
            startActivity(intent)
        }

        content7TextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.naver.com/jiwonph/222897247120"))
            startActivity(intent)
        }


    }
}
