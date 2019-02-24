package eu.uw.perfsite

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

private const val EXTRA_ENTRIES = "path"

@Suppress("UNUSED_EXPRESSION")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
        setContentView(R.layout.activity_main)
    }

    fun searchClick(my_view: View) {
        val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK && resultCode != RESULT_CANCELED && null != data) {

            // Recupère le chemin du fichier
            val tmpPath: String
            val selectedFile = data?.data
            tmpPath = selectedFile!!.path.split(":")[1]

            // Lance la KPIActivity en lui envoyant le chemin
            val intentKPI = Intent(this, KPIActivity::class.java)
            intentKPI.putExtra(EXTRA_ENTRIES, tmpPath)
            startActivity(intentKPI)
        }
    }

}
