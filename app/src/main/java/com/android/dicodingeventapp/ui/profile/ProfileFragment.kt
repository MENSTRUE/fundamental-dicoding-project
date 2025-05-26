package com.android.dicodingeventapp.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.android.dicodingeventapp.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnInstagram = view.findViewById<ImageButton>(R.id.btn_instagram)
        val btnTiktok = view.findViewById<ImageButton>(R.id.btn_tiktok)
        val btnWhatsapp = view.findViewById<ImageButton>(R.id.btn_whatsapp)
        val btnLinkedin = view.findViewById<ImageButton>(R.id.btn_linkedin)

        btnInstagram.setOnClickListener {
            val url = "https://www.instagram.com/syfrkhmn.z?igsh=anpzcTl3Y2l2cjJo"
            openUrl(url)
        }

        btnTiktok.setOnClickListener {
            val url = "https://www.tiktok.com/@gesukux_gaming?_t=ZS-8weNfipPlmE&_r=1"
            openUrl(url)
        }

        btnWhatsapp.setOnClickListener {
            val url = "https://wa.me/qr/5NPV3U7CFRTSF1"
            openUrl(url)
        }

        btnLinkedin.setOnClickListener{
            val url = "https://www.linkedin.com/in/wafa-bila-syaefurokhman-61b1a0328?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app"
            openUrl(url)
        }

    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
